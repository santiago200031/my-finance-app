package com.mobilecomputing.myfinance.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.data.service.EntryService
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Date

data class AnalysisUiState(
    val currentMonthSpending: Double = 0.0,
    val fixedContractExpenses: Double = 0.0,
    val totalMonthlyEarnings: Double = 0.0,
    val selectedDate: Date = Date()
)

class AnalysisViewModel(entryService: EntryService, private val contractService: ContractService) :
    ViewModel() {

    private val _selectedDate = MutableStateFlow(Date())

    val uiState: StateFlow<AnalysisUiState> =
        combine(
            entryService.getAllEntries(),
            contractService.getAllContracts(),
            _selectedDate
        ) { entries, contracts, selectedDate ->
            val currentMonthEntries =
                entries.filter { entry ->
                    DateUtils.isSameMonth(entry.date, selectedDate)
                }

            val spendingEntries = currentMonthEntries.filter { it.type == EntryType.EXPENSE }
            val currentMonthSpending = spendingEntries.sumOf { it.amount }

            // Filter contracts active in the selected month
            val startOfMonth = DateUtils.getStartOfMonth(selectedDate)
            val endOfMonth = DateUtils.getEndOfMonth(selectedDate)

            val activeContracts =
                contracts.filter { contract ->
                    contract.startDate <= endOfMonth &&
                            (contract.endDate == null ||
                                    contract.endDate >= startOfMonth)
                }

            val fixedContractExpenses =
                contractService.getTotalMonthlyCost(activeContracts)

            // Total Monthly Earnings
            // Contract Income (Monthly) + Entry Income
            val contractIncome = contractService.getTotalMonthlyIncome(activeContracts)
            val entryIncome =
                currentMonthEntries.filter { it.type == EntryType.INCOME }.sumOf {
                    it.amount
                }

            val totalEarnings = contractIncome + entryIncome

            AnalysisUiState(
                currentMonthSpending = currentMonthSpending,
                fixedContractExpenses = fixedContractExpenses,
                totalMonthlyEarnings = totalEarnings,
                selectedDate = selectedDate
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AnalysisUiState()
            )

    fun updateMonth(increment: Long) {
        _selectedDate.update { DateUtils.addMonths(it, increment) }
    }
}
