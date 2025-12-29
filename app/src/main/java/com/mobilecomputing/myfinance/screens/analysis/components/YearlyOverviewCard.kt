package com.mobilecomputing.myfinance.screens.analysis.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.RedExpense
import com.mobilecomputing.myfinance.utils.AppConstants
import com.mobilecomputing.myfinance.utils.FormatUtils

@Composable
fun YearlyOverviewCard(yearlySpending: Double, yearlyEarnings: Double, currency: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppConstants.PADDING_MEDIUM),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppConstants.PADDING_MEDIUM),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yearly Projection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val net = yearlyEarnings - yearlySpending
                val netColor = if (net >= 0) GreenIncome else RedExpense
                Text(
                    text = FormatUtils.formatCurrency(net, currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = netColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                YearlyStatItem(
                    label = "Projected Income",
                    amount = yearlyEarnings,
                    currency = currency,
                    color = GreenIncome
                )
                YearlyStatItem(
                    label = "Projected Spending",
                    amount = yearlySpending,
                    currency = currency,
                    color = RedExpense
                )
            }
        }
    }
}

@Composable
fun YearlyStatItem(label: String, amount: Double, currency: String, color: Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = FormatUtils.formatCurrency(amount, currency),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
