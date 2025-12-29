package com.mobilecomputing.myfinance.screens.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.analysis.components.CategoryAnalysisCard
import com.mobilecomputing.myfinance.screens.analysis.components.MonthlyOverviewSection
import com.mobilecomputing.myfinance.screens.analysis.components.YearlyOverviewCard
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.components.MonthYearSelector

@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    AnalysisScreenContent(
        uiState = uiState,
        onPreviousMonth = { viewModel.updateMonth(-1) },
        onNextMonth = { viewModel.updateMonth(1) }
    )
}

@Composable
fun AnalysisScreenContent(
    uiState: AnalysisUiState,
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {}
) {
    Scaffold { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
        ) {
            MonthYearSelector(
                currentDate = uiState.selectedDate,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            YearlyOverviewCard(
                yearlySpending = uiState.yearlySpending,
                yearlyEarnings = uiState.yearlyEarnings,
                currency = uiState.currency
            )

            MonthlyOverviewSection(
                currentMonthSpending = uiState.currentMonthSpending,
                fixedContractExpenses = uiState.fixedContractExpenses,
                totalMonthlyEarnings = uiState.totalMonthlyEarnings,
                currency = uiState.currency
            )

            CategoryAnalysisCard(
                title = "Expenses by Category",
                categories = uiState.expenseCategories,
                currency = uiState.currency
            )

            CategoryAnalysisCard(
                title = "Income by Category",
                categories = uiState.incomeCategories,
                currency = uiState.currency
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalysisScreenPreview() {
    AnalysisScreenContent(
        uiState =
            AnalysisUiState(
                currentMonthSpending = 450.00,
                fixedContractExpenses = 1200.00,
                totalMonthlyEarnings = 2500.00,
                yearlySpending = 18000.0,
                yearlyEarnings = 30000.0
            )
    )
}
