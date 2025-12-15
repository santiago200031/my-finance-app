package com.mobilecomputing.myfinance.screens.contracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.FinanceFilter
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.service.ContractService
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

data class ContractsUiState(
        val contracts: List<Contract> = emptyList(),
        val filter: FinanceFilter = FinanceFilter.ALL,
        val activeCount: Int = 0,
        val expiringCount: Int = 0,
        val monthlyNetValue: Double = 0.0
)

class ContractsViewModel(
        private val contractService: ContractService,
        private val userRepository: UserRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(FinanceFilter.ALL)
    private val _currentUserId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ContractsUiState> =
            combine(_currentUserId, _filter) { userId, filter -> Pair(userId, filter) }
                    .flatMapLatest { (userId, filter) ->
                        val contractsFlow =
                                if (userId == null) {
                                    contractService.getAllContracts()
                                } else {
                                    contractService.getContractsForUser(userId)
                                }

                        contractsFlow.map { contracts ->
                            val filteredContracts =
                                    contracts.filter { contract ->
                                        when (filter) {
                                            FinanceFilter.ALL -> true
                                            FinanceFilter.INCOME ->
                                                    contract.type == ContractType.INCOME
                                            FinanceFilter.EXPENSE ->
                                                    contract.type == ContractType.EXPENSE
                                            FinanceFilter.DEBT -> contract.type == ContractType.DEBT
                                        }
                                    }

                            ContractsUiState(
                                    contracts = filteredContracts,
                                    filter = filter,
                                    activeCount = contractService.getActiveCount(contracts),
                                    expiringCount = contractService.getExpiringCount(contracts),
                                    monthlyNetValue = contractService.getNetMonthlyValue(contracts)
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

    fun onFilterChanged(filter: FinanceFilter) {
        _filter.update { filter }
    }

    fun switchUser(userId: String) {
        _currentUserId.value = userId
    }

    fun resetToCurrentUser() {
        _currentUserId.value = null
    }

    suspend fun resolveUserIdFromEmail(email: String): String? {
        return userRepository.getUserByEmail(email)?.id
    }
}
