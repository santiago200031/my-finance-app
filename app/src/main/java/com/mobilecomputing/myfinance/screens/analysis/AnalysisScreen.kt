package com.mobilecomputing.myfinance.screens.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.components.AnalysisCard
import com.mobilecomputing.myfinance.ui.components.MonthYearSelector
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.RedExpense

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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MonthYearSelector(
                currentDate = uiState.selectedDate,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            AnalysisCard(
                title = "Spending",
                amount = uiState.currentMonthSpending,
                currency = uiState.currency,
                color = RedExpense,
                description = "Total expenses from entries"
            )

            AnalysisCard(
                title = "Fixed Contracts",
                amount = uiState.fixedContractExpenses,
                currency = uiState.currency,
                color = RedExpense,
                description = "Recurring expenses contribution"
            )

            AnalysisCard(
                title = "Total Earnings",
                amount = uiState.totalMonthlyEarnings,
                currency = uiState.currency,
                color = GreenIncome,
                description = "Contracts + Income entries"
            )
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
                totalMonthlyEarnings = 2500.00
            )
    )
}
