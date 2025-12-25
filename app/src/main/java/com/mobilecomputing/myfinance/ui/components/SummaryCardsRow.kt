package com.mobilecomputing.myfinance.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mobilecomputing.myfinance.screens.contracts.components.SummaryCard
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.Orange
import com.mobilecomputing.myfinance.ui.theme.RedExpense
import com.mobilecomputing.myfinance.utils.AppConstants
import com.mobilecomputing.myfinance.utils.FormatUtils

@Composable
fun SummaryCardsRow(activeCount: Int, expiringCount: Int, monthlyNetValue: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppConstants.PADDING_MEDIUM)
    ) {
        SummaryCard(
            title = "Active",
            value = activeCount.toString(),
            color = GreenIncome,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Expiring",
            value = expiringCount.toString(),
            color = Orange,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Monthly Net",
            value = FormatUtils.formatCurrency(monthlyNetValue),
            color = if (monthlyNetValue >= 0) GreenIncome else RedExpense,
            modifier = Modifier.weight(1.2f)
        )
    }
}
