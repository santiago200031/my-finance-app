package com.mobilecomputing.myfinance.screens.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.UserRepository
import com.mobilecomputing.myfinance.data.service.ContractService
import com.mobilecomputing.myfinance.data.service.EntryService
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.Calendar
import java.util.Date

data class AnalysisUiState(
    val currentMonthSpending: Double = 0.0,
    val fixedContractExpenses: Double = 0.0,
    val totalMonthlyEarnings: Double = 0.0,
    val selectedDate: Date = Date(),
    val currency: String = "EUR (€)",
    val yearlySpending: Double = 0.0,
    val yearlyEarnings: Double = 0.0,
    val expenseCategories: List<CategoryTotal> = emptyList(),
    val incomeCategories: List<CategoryTotal> = emptyList()
)

data class CategoryTotal(
    val categoryName: String,
    val amount: Double,
    val colorHex: String,
    val type: ContractType
)

class AnalysisViewModel(
    entryService: EntryService,
    private val contractService: ContractService,
    categoryRepository: CategoryRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Date())

    val uiState: StateFlow<AnalysisUiState> =
        combine(
            entryService.getAllEntries(),
            contractService.getAllContracts(),
            categoryRepository.getAllCategories(),
            userRepository.getCurrentUser(),
            _selectedDate
        ) { entries, contracts, categories, user, selectedDate ->
            val currentMonthSpending =
                calculateCurrentMonthSpending(entries, selectedDate)
            val fixedContractExpenses =
                calculateFixedContractExpenses(contracts, selectedDate)
            val totalMonthlyEarnings =
                calculateTotalMonthlyEarnings(contracts, entries, selectedDate)

            // Category Analysis
            val expenseCategories =
                calculateCategoryTotals(
                    entries = entries,
                    categories = categories,
                    selectedDate = selectedDate,
                    type = EntryType.EXPENSE
                )

            val incomeCategories =
                calculateCategoryTotals(
                    entries = entries,
                    categories = categories,
                    selectedDate = selectedDate,
                    type = EntryType.INCOME
                )

            // Yearly Overview
            val adjustment = calculateYearlyOverview(contracts, selectedDate)

            AnalysisUiState(
                currentMonthSpending = currentMonthSpending,
                fixedContractExpenses = fixedContractExpenses,
                totalMonthlyEarnings = totalMonthlyEarnings,
                selectedDate = selectedDate,
                currency = user?.settings?.currency ?: "EUR (€)",
                yearlySpending = adjustment.first,
                yearlyEarnings = adjustment.second,
                expenseCategories = expenseCategories,
                incomeCategories = incomeCategories
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

    private fun calculateCurrentMonthSpending(entries: List<Entry>, selectedDate: Date): Double {
        return entries
            .filter {
                DateUtils.isSameMonth(it.date, selectedDate) && it.type == EntryType.EXPENSE
            }
            .sumOf { it.amount }
    }

    private fun calculateFixedContractExpenses(
        contracts: List<Contract>,
        selectedDate: Date
    ): Double {
        val activeContracts = getActiveContracts(contracts, selectedDate)
        return contractService.getTotalMonthlyCost(activeContracts)
    }

    private fun calculateTotalMonthlyEarnings(
        contracts: List<Contract>,
        entries: List<Entry>,
        selectedDate: Date
    ): Double {
        val activeContracts = getActiveContracts(contracts, selectedDate)
        val contractIncome = contractService.getTotalMonthlyIncome(activeContracts)
        val entryIncome =
            entries
                .filter {
                    DateUtils.isSameMonth(it.date, selectedDate) &&
                            it.type == EntryType.INCOME
                }
                .sumOf { it.amount }
        return contractIncome + entryIncome
    }

    private fun getActiveContracts(contracts: List<Contract>, selectedDate: Date): List<Contract> {
        val startOfMonth = DateUtils.getStartOfMonth(selectedDate)
        val endOfMonth = DateUtils.getEndOfMonth(selectedDate)
        return contracts.filter { contract ->
            contract.startDate <= endOfMonth &&
                    (contract.endDate == null || contract.endDate >= startOfMonth)
        }
    }

    private fun calculateCategoryTotals(
        entries: List<Entry>,
        categories: List<Category>,
        selectedDate: Date,
        type: EntryType
    ): List<CategoryTotal> {
        val contractType =
            if (type == EntryType.EXPENSE) ContractType.EXPENSE else ContractType.INCOME

        return entries
            .filter { DateUtils.isSameMonth(it.date, selectedDate) && it.type == type }
            .groupBy { it.categoryId }
            .mapNotNull { (categoryId, categoryEntries) ->
                categories.find { it.id == categoryId }?.let { category ->
                    CategoryTotal(
                        categoryName = category.title,
                        amount = categoryEntries.sumOf { it.amount },
                        colorHex = category.colorHex,
                        type = contractType
                    )
                }
            }
            .sortedByDescending { it.amount }
    }

    private fun calculateYearlyOverview(
        contracts: List<Contract>,
        selectedDate: Date
    ): Pair<Double, Double> {
        val yearStart = DateUtils.getStartOfYear(selectedDate)
        val yearEnd = DateUtils.getEndOfYear(selectedDate)

        // year
        val relevantContracts =
            contracts.filter { contract ->
                val typeMatches =
                    contract.type == ContractType.EXPENSE ||
                            contract.type == ContractType.INCOME ||
                            contract.type == ContractType.DEBT

                val isStatusActive =
                    contract.status == ContractStatus.ACTIVE

                val overlapsYear =
                    (contract.endDate == null || contract.endDate >= yearStart) &&
                            contract.startDate <= yearEnd

                typeMatches && isStatusActive && overlapsYear
            }

        var yearlySpending = 0.0
        var yearlyEarnings = 0.0

        relevantContracts.forEach { contract ->
            val amountForYear = calculateContractAmountForYear(contract, yearStart, yearEnd)

            if (contract.type == ContractType.INCOME) {
                yearlyEarnings += amountForYear
            } else {
                // Expense or Debt
                yearlySpending += amountForYear
            }
        }

        return Pair(yearlySpending, yearlyEarnings)
    }

    private fun calculateContractAmountForYear(
        contract: Contract,
        yearStart: Date,
        yearEnd: Date
    ): Double {
        // Determine the effective period calculation within the year
        val effectiveStart = if (contract.startDate > yearStart) contract.startDate else yearStart
        val contractEnd = contract.endDate
        val effectiveEnd =
            if (contractEnd != null && contractEnd < yearEnd) contractEnd else yearEnd

        if (effectiveStart > effectiveEnd) return 0.0

        val cal = Calendar.getInstance()
        cal.time = contract.startDate

        var occurrences = 0
        var currentPaymentDate = cal.time

        // Fast forward to year start if needed
        while (currentPaymentDate < yearStart) {
            currentPaymentDate = getNextPaymentDate(currentPaymentDate, contract.paymentCycle)
        }

        // Count limits
        // For DEBT: effectively limited by effectiveEnd (which is min(contract.endDate, yearEnd))
        // This ensures we stop calculating when the debt is paid off (contract.endDate).
        while (currentPaymentDate <= effectiveEnd) {
            occurrences++
            currentPaymentDate = getNextPaymentDate(currentPaymentDate, contract.paymentCycle)
        }

        return occurrences * contract.amount
    }

    private fun getNextPaymentDate(date: Date, cycle: PaymentCycle): Date {
        return when (cycle) {
            PaymentCycle.WEEKLY -> DateUtils.addDays(date, 7)
            PaymentCycle.MONTHLY -> DateUtils.addMonths(date, 1)
            PaymentCycle.QUARTERLY -> DateUtils.addMonths(date, 3)
            PaymentCycle.YEARLY ->
                DateUtils.addMonths(
                    date,
                    12
                ) // Approximate, DateUtils addYears would be better if exists
        }
    }
}
