package com.mobilecomputing.myfinance.screens.add_contract

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.data.repository.ContractRepository
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import com.mobilecomputing.myfinance.domain.ContractService
import com.mobilecomputing.myfinance.utils.DateUtils
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddContractUiState(
        val title: String = "",
        val amount: String = "",
        val totalAmount: String = "",
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
        private val contractRepository: ContractRepository,
        private val contractService: ContractService,
        private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddContractUiState())
    val uiState: StateFlow<AddContractUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onTotalAmountChange(amount: String) {
        _uiState.update { it.copy(totalAmount = amount) }
    }

    fun onStartDateChange(date: String) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun onExpirationDateChange(date: String) {
        _uiState.update { it.copy(expirationDate = date) }
    }

    fun onCycleSelect(cycle: PaymentCycle) {
        _uiState.update { it.copy(billingCycle = cycle) }
    }

    fun onTypeSelect(type: ContractType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun onAutoRenewChange(isAutoRenew: Boolean) {
        _uiState.update { it.copy(isAutoRenew = isAutoRenew) }
    }

    fun onStatusChange(status: ContractStatus) {
        _uiState.update { it.copy(status = status) }
    }

    fun loadContract(contractId: String) {
        viewModelScope.launch {
            val contract = contractRepository.getContractById(contractId).first()
            if (contract != null) {
                _uiState.update {
                    it.copy(
                            title = contract.title,
                            amount = contract.amount.toString(),
                            // totalAmount field missing in new Contract model
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
        // val totalAmountValue = currentState.totalAmount.toDoubleOrNull() // Not used in new model

        if (amountValue != null && currentState.title.isNotBlank()) {

            var start = LocalDate.now()
            try {
                start = DateUtils.parseInputDate(currentState.startDate)
            } catch (e: DateTimeParseException) {
                // Fallback to today if parsing fails
            }

            var end: LocalDate? = null
            if (currentState.expirationDate.isNotBlank()) {
                try {
                    end = DateUtils.parseInputDate(currentState.expirationDate)
                } catch (e: DateTimeParseException) {
                    // Ignore validity check for simplicity
                }
            }

            val nextPayment =
                    start.plusMonths(
                            if (currentState.billingCycle == PaymentCycle.MONTHLY) 1 else 12
                    )

            viewModelScope.launch {
                val newContract =
                        Contract(
                                id = currentState.contractId
                                                ?: java.util.UUID.randomUUID().toString(),
                                title = currentState.title,
                                amount = amountValue,
                                // totalAmount = if (currentState.selectedType == ContractType.DEBT)
                                // totalAmountValue else null,
                                paymentCycle = currentState.billingCycle,
                                type = currentState.selectedType,
                                startDate =
                                        Date.from(
                                                start.atStartOfDay(ZoneId.systemDefault())
                                                        .toInstant()
                                        ),
                                endDate =
                                        end?.let {
                                            Date.from(
                                                    it.atStartOfDay(ZoneId.systemDefault())
                                                            .toInstant()
                                            )
                                        },
                                nextPaymentDate =
                                        Date.from(
                                                nextPayment
                                                        .atStartOfDay(ZoneId.systemDefault())
                                                        .toInstant()
                                        ),
                                autoRenewEnabled = currentState.isAutoRenew,
                                status = currentState.status
                        )

                // Check status logic (e.g. if created expired or auto-renew logic immediately
                // applies)
                val checkedContract = contractService.checkContractStatus(newContract)

                if (currentState.contractId != null) {
                    contractRepository.updateContract(checkedContract)
                } else {
                    contractRepository.addContract(checkedContract)
                }
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun deleteContract() {
        val contractId = _uiState.value.contractId
        if (contractId != null) {
            viewModelScope.launch {
                contractRepository.deleteContract(contractId)
                reminderRepository.deleteRemindersForContract(contractId)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(isSaved = false) }
    }
}
