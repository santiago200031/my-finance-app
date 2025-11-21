package com.mobilecomputing.myfinance.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.screens.dashboard.components.BalanceSummaryCard
import com.mobilecomputing.myfinance.screens.dashboard.components.TransactionItem
import com.mobilecomputing.myfinance.screens.dashboard.data.getMockTransactions

@Composable
fun DashboardScreen() {
    DashboardOverviewMockup()
}

@Composable
fun DashboardOverviewMockup() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BalanceSummaryCard(
                title = "Income",
                amount = "$4,500.00",
                modifier = Modifier.weight(1f)
            )
            BalanceSummaryCard(
                title = "Expenses",
                amount = "$1,250.50",
                modifier = Modifier.weight(1f)
            )
            BalanceSummaryCard(
                title = "Net Growth",
                amount = "$3,249.50",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Pie Chart Placeholder", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Recent Transactions", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getMockTransactions()) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardOverviewMockupPreview() {
    DashboardOverviewMockup()
}
