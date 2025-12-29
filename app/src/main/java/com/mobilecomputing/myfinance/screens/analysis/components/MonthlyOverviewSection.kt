package com.mobilecomputing.myfinance.screens.analysis.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.ui.components.AnalysisCard
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.RedExpense
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun MonthlyOverviewSection(
    currentMonthSpending: Double,
    fixedContractExpenses: Double,
    totalMonthlyEarnings: Double,
    currency: String
) {
    Column {
        Text(
            text = "Monthly Overview",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier =
                Modifier.padding(
                    horizontal = AppConstants.PADDING_MEDIUM,
                    vertical = AppConstants.PADDING_SMALL
                )
        )

        Box(
            modifier =
                Modifier.padding(horizontal = AppConstants.PADDING_MEDIUM, vertical = 4.dp)
        ) {
            AnalysisCard(
                title = "Spending",
                amount = currentMonthSpending,
                currency = currency,
                color = RedExpense,
                description = "Total expenses from entries"
            )
        }

        Box(
            modifier =
                Modifier.padding(horizontal = AppConstants.PADDING_MEDIUM, vertical = 4.dp)
        ) {
            AnalysisCard(
                title = "Fixed Contracts",
                amount = fixedContractExpenses,
                currency = currency,
                color = RedExpense,
                description = "Recurring expenses contribution"
            )
        }

        Box(
            modifier =
                Modifier.padding(horizontal = AppConstants.PADDING_MEDIUM, vertical = 4.dp)
        ) {
            AnalysisCard(
                title = "Total Earnings",
                amount = totalMonthlyEarnings,
                currency = currency,
                color = GreenIncome,
                description = "Contracts + Income entries"
            )
        }
    }
}
