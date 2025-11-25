package com.mobilecomputing.myfinance.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.data.models.transaction.TransactionType
import com.mobilecomputing.myfinance.screens.dashboard.data.getMockTransactions
import com.mobilecomputing.myfinance.ui.components.BalanceSummaryCard
import com.mobilecomputing.myfinance.ui.components.TransactionItem


@Composable
@Preview(showBackground = true)
fun DashboardScreen() {
    fun remindersOnClick() {
    }

    fun addEntryOnClick() {
    }

    val transactions = getMockTransactions()

    val totalIncome =
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpenses =
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    val netGrowth = totalIncome - totalExpenses

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
                amount = "$${String.format("%.2f", totalIncome)}",
                modifier = Modifier.weight(1f)
            )
            BalanceSummaryCard(
                title = "Expenses",
                amount = "$${String.format("%.2f", totalExpenses)}",
                modifier = Modifier.weight(1f)
            )
            BalanceSummaryCard(
                title = "Net Growth",
                amount = "$${String.format("%.2f", netGrowth)}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Quick Actions",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = { addEntryOnClick() },
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add new entry",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Add Entry")
                    }
                    OutlinedButton(
                        onClick = { remindersOnClick() },
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Check reminders"
                        )
                        Text(
                            "Reminders"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Recent Activity",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }

}