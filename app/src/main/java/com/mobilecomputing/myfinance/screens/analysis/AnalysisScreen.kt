package com.mobilecomputing.myfinance.screens.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.components.AnalysisCard
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.RedExpense
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable

fun AnalysisScreen(
    viewModel: AnalysisViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    AnalysisScreenContent(uiState = uiState)
}

@Composable
fun AnalysisScreenContent(uiState: AnalysisUiState) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppConstants.PADDING_MEDIUM),
            verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_MEDIUM)
        ) {
            Text(
                text = "Financial Overview",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = AppConstants.PADDING_SMALL)
            )

            AnalysisCard(
                title = "Spending (Current Month)",
                amount = uiState.currentMonthSpending,
                color = RedExpense,
                description = "Total expenses from entries this month"
            )

            AnalysisCard(
                title = "Fixed Contracts (Monthly)",
                amount = uiState.fixedContractExpenses,
                color = RedExpense,
                description = "Recurring expenses contribution"
            )

            AnalysisCard(
                title = "Total Earnings (Monthly)",
                amount = uiState.totalMonthlyEarnings,
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
        uiState = AnalysisUiState(
            currentMonthSpending = 450.0,
            fixedContractExpenses = 1200.0,
            totalMonthlyEarnings = 2500.0
        )
    )
}

