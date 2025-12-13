package com.mobilecomputing.myfinance.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import com.mobilecomputing.myfinance.ui.models.TransactionUiModel
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
        val transactions: List<TransactionUiModel> = emptyList(),
        val totalIncome: Double = 0.0,
        val totalExpenses: Double = 0.0,
        val netGrowth: Double = 0.0
)

class DashboardViewModel(
        private val entryRepository: EntryRepository,
        private val categoryRepository: CategoryRepository
) : ViewModel() {

        val uiState: StateFlow<DashboardUiState> =
                combine(entryRepository.getAllEntries(), categoryRepository.getAllCategories()) {
                                entries,
                                categories ->
                                val totalIncome =
                                        entries.filter { it.type == ContractType.INCOME }.sumOf {
                                                it.amount
                                        }
                                val totalExpenses =
                                        entries
                                                .filter {
                                                        it.type == ContractType.EXPENSE ||
                                                                it.type == ContractType.DEBT
                                                }
                                                .sumOf { it.amount }
                                val netGrowth = totalIncome - totalExpenses

                                val uiTransactions =
                                        entries.sortedByDescending { it.date }.map { entry ->
                                                val category =
                                                        categories.find {
                                                                it.id == entry.categoryId
                                                        }
                                                TransactionUiModel(
                                                        id = entry.id,
                                                        amount = entry.amount,
                                                        description = entry.description
                                                                        ?: "No Description",
                                                        date = entry.date,
                                                        categoryName = category?.title
                                                                        ?: "Uncategorized",
                                                        type = entry.type,
                                                        categoryId = entry.categoryId,
                                                        formattedDate =
                                                                DateUtils.formatDate(entry.date)
                                                )
                                        }

                                DashboardUiState(
                                        transactions = uiTransactions,
                                        totalIncome = totalIncome,
                                        totalExpenses = totalExpenses,
                                        netGrowth = netGrowth
                                )
                        }
                        .stateIn(
                                scope = viewModelScope,
                                started = SharingStarted.WhileSubscribed(5000),
                                initialValue = DashboardUiState()
                        )
}
