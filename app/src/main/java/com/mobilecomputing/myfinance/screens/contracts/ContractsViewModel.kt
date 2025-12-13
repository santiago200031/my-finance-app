package com.mobilecomputing.myfinance.screens.contracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.FinanceFilter
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.repository.ContractRepository
import com.mobilecomputing.myfinance.domain.ContractService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
        private val contractRepository: ContractRepository,
        private val contractService: ContractService
) : ViewModel() {

    private val _filter = MutableStateFlow(FinanceFilter.ALL)

    val uiState: StateFlow<ContractsUiState> =
            combine(contractRepository.getAllContracts(), _filter) { contracts, filter ->
                        val filteredContracts =
                                contracts.filter { contract ->
                                    when (filter) {
                                        FinanceFilter.ALL -> true
                                        FinanceFilter.INCOME -> contract.type == ContractType.INCOME
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
            val currentContracts = contractRepository.getAllContracts().first()

            currentContracts.forEach { contract ->
                val updatedContract = contractService.checkContractStatus(contract)
                if (updatedContract != contract) {
                    contractRepository.updateContract(updatedContract)
                }
            }
        }
    }

    fun onFilterChanged(filter: FinanceFilter) {
        _filter.update { filter }
    }
}
