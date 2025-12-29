package com.mobilecomputing.myfinance.screens.contracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.contract.ContractFilter
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.screens.contracts.data.ContractsData
import com.mobilecomputing.myfinance.screens.contracts.data.ContractsUiState
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class ContractsViewModel(
    private val contractService: ContractService,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(ContractFilter.ALL)
    private val _currentUserId = MutableStateFlow<String?>(null)
    private val _selectedDate = MutableStateFlow(Date())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ContractsUiState> =
        combine(_currentUserId, _filter, _selectedDate, userRepository.getCurrentUser()) { userId,
                                                                                           filter,
                                                                                           selectedDate,
                                                                                           currentUser ->
            ContractsData(userId, filter, selectedDate, currentUser)
        }
            .flatMapLatest { data: ContractsData ->
                val userId = data.userId
                val filter = data.filter
                val selectedDate = data.selectedDate
                val currentUser = data.currentUser
                val contractsFlow =
                    if (userId == null) {
                        contractService.getAllContracts()
                    } else {
                        // Verify trust
                        val targetUser = userRepository.getUserById(userId)
                        val isTrusted =
                            targetUser?.trustedEmails?.contains(
                                currentUser?.email
                            ) == true
                        if (isTrusted) {
                            contractService.getContractsForUser(userId)
                        } else {
                            // Return empty flow if not trusted
                            kotlinx.coroutines.flow.flowOf(emptyList())
                        }
                    }

                contractsFlow.map { contracts ->
                    val filteredContracts =
                        contracts.filter { contract ->
                            val matchesFilter =
                                when (filter) {
                                    ContractFilter.ALL -> true
                                    ContractFilter.INCOME ->
                                        contract.type == ContractType.INCOME

                                    ContractFilter.EXPENSE ->
                                        contract.type == ContractType.EXPENSE

                                    ContractFilter.DEBT ->
                                        contract.type == ContractType.DEBT
                                }

                            val startOfMonth = DateUtils.getStartOfMonth(selectedDate)
                            val endOfMonth = DateUtils.getEndOfMonth(selectedDate)

                            val isActiveInMonth =
                                contract.startDate <= endOfMonth &&
                                        (contract.endDate == null ||
                                                contract.endDate >= startOfMonth)

                            matchesFilter && isActiveInMonth
                        }

                    ContractsUiState(
                        contracts = filteredContracts,
                        filter = filter,
                        selectedDate = selectedDate,
                        activeCount = contractService.getActiveCount(contracts),
                        expiringCount = contractService.getExpiringCount(contracts),
                        monthlyNetValue = contractService.getNetMonthlyValue(contracts),
                        currency = currentUser?.settings?.currency ?: "EUR (€)",
                        dateFormat = currentUser?.settings?.dateFormat ?: "dd.MM.yyyy"
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ContractsUiState()
            )

    init {
        refreshContractStatuses()
    }

    fun refreshContractStatuses() {
        viewModelScope.launch {
            val currentContracts = contractService.getAllContracts().first()

            currentContracts.forEach { contract ->
                val updatedContract = contractService.checkContractStatus(contract)
                if (updatedContract != contract) {
                    contractService.updateContract(updatedContract)
                }
            }
        }
    }

    fun onFilterChanged(filter: ContractFilter) {
        _filter.update { filter }
    }

    fun updateMonth(increment: Long) {
        _selectedDate.update { DateUtils.addMonths(it, increment) }
    }

    fun switchUser(userId: String) {
        _currentUserId.value = userId
    }

    suspend fun resolveUserIdFromEmail(email: String): String? {
        return userRepository.getUserByEmail(email)?.id
    }
}
