package com.mobilecomputing.myfinance.screens.budget_planning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.service.BudgetOverview
import com.mobilecomputing.myfinance.data.service.BudgetService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface BudgetUiState {
    object Loading : BudgetUiState
    data class Success(val overview: BudgetOverview) : BudgetUiState
    data class Error(val message: String) : BudgetUiState
}

class BudgetPlanningViewModel(private val budgetService: BudgetService) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentYear = calendar.get(Calendar.YEAR)

    val uiState: StateFlow<BudgetUiState> =
        budgetService
            .getBudgetOverview(currentMonth, currentYear)
            .map { BudgetUiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = BudgetUiState.Loading
            )

    fun updateBudgetLimit(categoryId: String, newLimit: Double) {
        viewModelScope.launch { budgetService.updateCategoryBudget(categoryId, newLimit) }
    }

    fun addCategory(category: com.mobilecomputing.myfinance.data.category.Category) {
        viewModelScope.launch { budgetService.addCategory(category) }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch { budgetService.deleteCategory(categoryId) }
    }
}
