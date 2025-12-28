package com.mobilecomputing.myfinance.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.service.EntryService
import com.mobilecomputing.myfinance.ui.models.EntryUiModel
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val transactions: List<EntryUiModel> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netGrowth: Double = 0.0
)

class DashboardViewModel(entryService: EntryService, categoryRepository: CategoryRepository) :
    ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        combine(entryService.getAllEntries(), categoryRepository.getAllCategories()) { entries,
                                                                                       categories ->
            val totalIncome =
                entries.filter { it.type == EntryType.INCOME }.sumOf { it.amount }
            val totalExpenses =
                entries.filter { it.type == EntryType.EXPENSE }.sumOf { it.amount }
            val netGrowth = totalIncome - totalExpenses

            val uiTransactions =
                entries
                    .sortedByDescending { it.date }
                    .map { entry ->
                        val category =
                            categories.find { it.id == entry.categoryId }
                        EntryUiModel(
                            id = entry.id,
                            amount = entry.amount,
                            description = entry.description
                                ?: "No Description",
                            date = entry.date,
                            categoryName = category?.title
                                ?: "Uncategorized",
                            type = entry.type,
                            categoryId = entry.categoryId,
                            formattedDate = DateUtils.formatDate(entry.date)
                        )
                    }
                    .take(3)

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
