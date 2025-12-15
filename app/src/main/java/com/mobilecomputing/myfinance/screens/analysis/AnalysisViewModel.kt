package com.mobilecomputing.myfinance.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.data.service.EntryService
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AnalysisUiState(
        val currentMonthSpending: Double = 0.0,
        val fixedContractExpenses: Double = 0.0,
        val totalMonthlyEarnings: Double = 0.0
)

class AnalysisViewModel(
        private val entryService: EntryService,
        private val contractService: ContractService
) : ViewModel() {

        val uiState: StateFlow<AnalysisUiState> =
                combine(entryService.getAllEntries(), contractService.getAllContracts()) {
                                entries,
                                contracts ->
                                val now = LocalDate.now()

                                val currentMonthEntries =
                                        entries.filter { entry ->
                                                val instant = entry.date.toInstant()
                                                val zonedDateTime =
                                                        instant.atZone(ZoneId.systemDefault())
                                                val date = zonedDateTime.toLocalDate()
                                                date.month == now.month && date.year == now.year
                                        }

                                val spendingEntries =
                                        currentMonthEntries.filter {
                                                it.type == ContractType.EXPENSE ||
                                                        it.type == ContractType.DEBT
                                        }
                                val currentMonthSpending = spendingEntries.sumOf { it.amount }

                                // Fixed Contract Expenses (Contracts only, monthly normalized)
                                val fixedContractExpenses =
                                        contractService.getTotalMonthlyCost(contracts)

                                // Total Monthly Earnings
                                // Contract Income (Monthly) + Entry Income (Current Month)
                                val contractIncome =
                                        contractService.getTotalMonthlyIncome(contracts)
                                val entryIncome =
                                        currentMonthEntries
                                                .filter { it.type == ContractType.INCOME }
                                                .sumOf { it.amount }

                                val totalEarnings = contractIncome + entryIncome

                                AnalysisUiState(
                                        currentMonthSpending = currentMonthSpending,
                                        fixedContractExpenses = fixedContractExpenses,
                                        totalMonthlyEarnings = totalEarnings
                                )
                        }
                        .stateIn(
                                scope = viewModelScope,
                                started = SharingStarted.WhileSubscribed(5000),
                                initialValue = AnalysisUiState()
                        )
}
