package com.mobilecomputing.myfinance.screens.add_contract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.util.Date

data class AddContractUiState(
    val title: String = "",
    val provider: String = "",
    val amount: String = "",
    val totalAmount: String? = "",
    val startDate: String = DateUtils.formatInputDate(LocalDate.now()),
    val expirationDate: String = "",
    val billingCycle: PaymentCycle = PaymentCycle.MONTHLY,
    val selectedType: ContractType = ContractType.EXPENSE,
    val isSaved: Boolean = false,
    val contractId: String? = null,
    val isAutoRenew: Boolean = true,
    val status: ContractStatus = ContractStatus.ACTIVE
)

class AddContractViewModel(
    private val contractService: ContractService,
    private val reminderRepository: ReminderRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddContractUiState())
    val uiState: StateFlow<AddContractUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onProviderChange(provider: String) {
        _uiState.update { it.copy(provider = provider) }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
        // If Total Amount is present, recalculate End Date
        if (_uiState.value.totalAmount?.isNotBlank() == true) {
            calculateEndDateFromTotal()
        }
    }

    fun onTotalAmountChange(amount: String) {
        _uiState.update { it.copy(totalAmount = amount) }
        // If Total Amount is present, recalculate End Date
        calculateEndDateFromTotal()
    }

    fun onExpirationDateChange(date: String) {
        _uiState.update { it.copy(expirationDate = date) }
        calculateTotalFromEndDate()
    }

    private fun calculateEndDateFromTotal() {
        val currentState = _uiState.value
        if (currentState.selectedType != ContractType.DEBT) return

        val total = currentState.totalAmount?.toDoubleOrNull()
        val periodic = currentState.amount.toDoubleOrNull()

        if (total != null && periodic != null && periodic > 0) {
            try {
                // Use ceil to ensure we cover the debt
                val cycles = kotlin.math.ceil(total / periodic).toLong()
                if (cycles > 0) {
                    val startDate = DateUtils.parseInputDate(currentState.startDate)
                    val start =
                        Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

                    val endDate =
                        when (currentState.billingCycle) {
                            PaymentCycle.WEEKLY -> DateUtils.addWeeks(start, cycles)
                            PaymentCycle.MONTHLY -> DateUtils.addMonths(start, cycles)
                            PaymentCycle.QUARTERLY -> DateUtils.addMonths(start, cycles * 3)
                            PaymentCycle.YEARLY -> DateUtils.addMonths(start, cycles * 12)
                        }
                    val formattedEnd =
                        DateUtils.formatInputDate(
                            endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        )
                    _uiState.update { it.copy(expirationDate = formattedEnd) }
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun calculateTotalFromEndDate() {
        val currentState = _uiState.value
        if (currentState.selectedType != ContractType.DEBT) return

        val periodic = currentState.amount.toDoubleOrNull()
        if (periodic != null && periodic > 0 && currentState.expirationDate.isNotBlank()) {
            try {
                val startDate = DateUtils.parseInputDate(currentState.startDate)
                val endDate = DateUtils.parseInputDate(currentState.expirationDate)

                val start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                val end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

                if (end > start) {
                    val cycles =
                        when (currentState.billingCycle) {
                            PaymentCycle.WEEKLY -> DateUtils.getDifferenceInWeeks(start, end)
                            PaymentCycle.MONTHLY -> DateUtils.getDifferenceInMonths(start, end)
                            PaymentCycle.QUARTERLY ->
                                DateUtils.getDifferenceInMonths(start, end) / 3

                            PaymentCycle.YEARLY ->
                                DateUtils.getDifferenceInMonths(start, end) / 12
                        }
                    val total = cycles * periodic

                    // Only update if total > 0
                    if (total > 0) {
                        _uiState.update { it.copy(totalAmount = total.toString()) }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun onStartDateChange(date: String) {
        _uiState.update { it.copy(startDate = date) }

        if (_uiState.value.totalAmount?.isNotBlank() == true) {
            calculateEndDateFromTotal()
        }
    }

    fun onCycleSelect(cycle: PaymentCycle) {
        _uiState.update { it.copy(billingCycle = cycle) }
        if (_uiState.value.totalAmount?.isNotBlank() == true) {
            calculateEndDateFromTotal()
        }
    }

    fun onTypeSelect(type: ContractType) {
        _uiState.update {
            it.copy(
                selectedType = type,
                // Default auto-renew to false for debts, but keep true for subscriptions
                isAutoRenew = if (type == ContractType.DEBT) false else it.isAutoRenew
            )
        }
    }

    fun onAutoRenewChange(isAutoRenew: Boolean) {
        _uiState.update { it.copy(isAutoRenew = isAutoRenew) }
    }

    fun onStatusChange(status: ContractStatus) {
        _uiState.update { it.copy(status = status) }
    }

    fun loadContract(contractId: String) {
        viewModelScope.launch {
            val contract = contractService.getContractById(contractId).first()
            if (contract != null) {
                _uiState.update {
                    it.copy(
                        title = contract.title,
                        provider = contract.provider,
                        amount = contract.amount.toString(),
                        totalAmount = contract.totalAmount?.toString(),
                        startDate =
                            DateUtils.formatInputDate(
                                contract.startDate
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            ),
                        expirationDate =
                            contract.endDate?.let { date ->
                                DateUtils.formatInputDate(
                                    date.toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                )
                            }
                                ?: "",
                        billingCycle = contract.paymentCycle,
                        selectedType = contract.type,
                        contractId = contract.id,
                        isAutoRenew = contract.autoRenewEnabled,
                        status = contract.status
                    )
                }
            }
        }
    }

    fun saveContract() {
        val currentState = _uiState.value
        val amountValue = currentState.amount.toDoubleOrNull()

        if (amountValue != null && currentState.title.isNotBlank()) {

            // 1. Consistency Check for Debt
            val consistencyResult = ensureDebtConsistency(currentState, amountValue)
            if (consistencyResult == null && currentState.selectedType == ContractType.DEBT) {
                return
            }

            val finalTotalAmount =
                consistencyResult?.first ?: currentState.totalAmount?.toDoubleOrNull()
            val finalExpirationDate = consistencyResult?.second ?: currentState.expirationDate

            // 2. Parse Dates
            val start = parseDate(currentState.startDate) ?: LocalDate.now()
            val end = if (finalExpirationDate.isNotBlank()) parseDate(finalExpirationDate) else null

            // 3. Calculate Next Payment
            val nextPayment = calculateNextPayment(start, currentState.billingCycle)

            viewModelScope.launch {
                var finalStatus = currentState.status

                if (end != null && end < LocalDate.now()) {
                    if (finalStatus == ContractStatus.ACTIVE ||
                        finalStatus == ContractStatus.EXPIRING
                    ) {
                        finalStatus = ContractStatus.OUTDATED
                    }
                }

                val currentUser = userRepository.getCurrentUser().first()
                val currentUserId = currentUser?.id ?: ""

                val newContract =
                    createContractObject(
                        currentState = currentState,
                        amountValue = amountValue,
                        totalAmountValue = finalTotalAmount,
                        currentUserId = currentUserId,
                        start = start,
                        end = end,
                        nextPayment = nextPayment,
                        status = finalStatus
                    )

                val checkedContract = contractService.checkContractStatus(newContract)

                if (currentState.contractId != null) {
                    contractService.updateContract(checkedContract)
                } else {
                    contractService.addContract(checkedContract)
                }
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    private fun ensureDebtConsistency(
        currentState: AddContractUiState,
        amountValue: Double
    ): Pair<Double?, String>? {
        if (currentState.selectedType != ContractType.DEBT) return null

        var totalAmountValue = currentState.totalAmount?.toDoubleOrNull()
        var finalExpirationDate = currentState.expirationDate

        // Case 1: Has Total, Missing End Date -> Calculate End Date
        if (totalAmountValue != null && totalAmountValue > 0 && finalExpirationDate.isBlank()) {
            try {
                val cycles = kotlin.math.ceil(totalAmountValue / amountValue).toLong()

                if (cycles > 0) {
                    val startDate = DateUtils.parseInputDate(currentState.startDate)
                    val start =
                        Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    val derivedEndDate =
                        when (currentState.billingCycle) {
                            PaymentCycle.WEEKLY -> DateUtils.addWeeks(start, cycles)
                            PaymentCycle.MONTHLY -> DateUtils.addMonths(start, cycles)
                            PaymentCycle.QUARTERLY -> DateUtils.addMonths(start, cycles * 3)
                            PaymentCycle.YEARLY -> DateUtils.addMonths(start, cycles * 12)
                        }
                    finalExpirationDate =
                        DateUtils.formatInputDate(
                            derivedEndDate
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                }
            } catch (_: Exception) {
            }
        }
        // Case 2: Has End Date, Missing Total -> Calculate Total
        else if (finalExpirationDate.isNotBlank() &&
            (totalAmountValue == null || totalAmountValue <= 0)
        ) {
            try {
                val startDate = DateUtils.parseInputDate(currentState.startDate)
                val endDate = DateUtils.parseInputDate(finalExpirationDate)
                val start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                val end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

                if (end > start) {
                    val cycles =
                        when (currentState.billingCycle) {
                            PaymentCycle.WEEKLY -> DateUtils.getDifferenceInWeeks(start, end)
                            PaymentCycle.MONTHLY -> DateUtils.getDifferenceInMonths(start, end)
                            PaymentCycle.QUARTERLY ->
                                DateUtils.getDifferenceInMonths(start, end) / 3

                            PaymentCycle.YEARLY ->
                                DateUtils.getDifferenceInMonths(start, end) / 12
                        }
                    totalAmountValue = cycles * amountValue
                }
            } catch (_: Exception) {
            }
        }

        // Final Validation check
        if ((totalAmountValue == null || totalAmountValue <= 0.0) && finalExpirationDate.isBlank()
        ) {
            return null // Invalid state for Debt
        }

        return Pair(totalAmountValue, finalExpirationDate)
    }

    private fun parseDate(dateString: String): LocalDate? {
        if (dateString.isBlank()) return null
        return try {
            DateUtils.parseInputDate(dateString)
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private fun calculateNextPayment(start: LocalDate, cycle: PaymentCycle): LocalDate {
        val monthsToAdd =
            when (cycle) {
                PaymentCycle.MONTHLY -> 1L
                PaymentCycle.QUARTERLY -> 3L
                PaymentCycle.YEARLY -> 12L
                PaymentCycle.WEEKLY -> 0L
            }

        return if (cycle == PaymentCycle.WEEKLY) {
            start.plusWeeks(1)
        } else {
            start.plusMonths(monthsToAdd)
        }
    }

    private fun createContractObject(
        currentState: AddContractUiState,
        amountValue: Double,
        totalAmountValue: Double?,
        currentUserId: String,
        start: LocalDate,
        end: LocalDate?,
        nextPayment: LocalDate,
        status: ContractStatus? = null
    ): Contract {
        return Contract(
            id = currentState.contractId ?: java.util.UUID.randomUUID().toString(),
            title = currentState.title,
            provider = currentState.provider, // Added provider
            amount = amountValue,
            totalAmount = totalAmountValue,
            paymentCycle = currentState.billingCycle,
            type = currentState.selectedType,
            userId = currentUserId,
            startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            endDate =
                end?.let { Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant()) },
            nextPaymentDate =
                Date.from(nextPayment.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            autoRenewEnabled = currentState.isAutoRenew,
            status = status ?: currentState.status
        )
    }

    fun deleteContract() {
        val contractId = _uiState.value.contractId
        if (contractId != null) {
            viewModelScope.launch {
                contractService.deleteContract(contractId)
                reminderRepository.deleteRemindersForContract(contractId)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(isSaved = false) }
    }
}
