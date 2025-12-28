package com.mobilecomputing.myfinance.data.service

import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar

data class CategoryBudgetStatus(
    val category: Category,
    val spentAmount: Double,
    val remainingAmount: Double,
    val percentUsed: Int
)

data class BudgetOverview(
    val totalBudget: Double,
    val totalSpent: Double,
    val percentUsed: Int,
    val categoryStatuses: List<CategoryBudgetStatus>
)

class BudgetService(
    private val categoryRepository: CategoryRepository,
    private val entryRepository: EntryRepository
) {
    fun getBudgetOverview(month: Int, year: Int): Flow<BudgetOverview> {
        return combine(
            categoryRepository.getAllCategories(),
            entryRepository.getAllEntries()
        ) { categories, entries ->
            val expenseCategories =
                categories.filter { it.type == ContractType.EXPENSE }

            val calendar = Calendar.getInstance()
            val filteredEntries =
                entries.filter { entry ->
                    calendar.time = entry.date
                    calendar.get(Calendar.MONTH) == month &&
                            calendar.get(Calendar.YEAR) == year &&
                            entry.type == EntryType.EXPENSE
                }

            val categoryStatuses =
                expenseCategories.map { category ->
                    val spent =
                        filteredEntries
                            .filter { it.categoryId == category.id }
                            .sumOf { it.amount }
                    val remaining = category.budgetLimit - spent
                    val percent =
                        if (category.budgetLimit > 0)
                            ((spent / category.budgetLimit) * 100)
                                .toInt()
                        else 0
                    CategoryBudgetStatus(category, spent, remaining, percent)
                }

            val totalBudget = expenseCategories.sumOf { it.budgetLimit }
            val totalSpent = filteredEntries.sumOf { it.amount }
            val totalPercent =
                if (totalBudget > 0) ((totalSpent / totalBudget) * 100).toInt()
                else 0

            BudgetOverview(totalBudget, totalSpent, totalPercent, categoryStatuses)
        }
    }

    suspend fun updateCategoryBudget(categoryId: String, newLimit: Double) {
        val category = categoryRepository.getCategoryById(categoryId)
        if (category != null) {
            categoryRepository.updateCategory(category.copy(budgetLimit = newLimit))
        }
    }
}
