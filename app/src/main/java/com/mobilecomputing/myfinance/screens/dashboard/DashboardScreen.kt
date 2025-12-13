package com.mobilecomputing.myfinance.screens.dashboard

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.dashboard.components.BalanceSummaryCard
import com.mobilecomputing.myfinance.screens.entries.components.TransactionItem
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.ui.theme.RedExpense
import com.mobilecomputing.myfinance.utils.FormatUtils

@Composable
fun DashboardScreen(
        onRemindersClick: () -> Unit = {},
        onAddEntryClick: () -> Unit = {},
        viewModel: DashboardViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
        val uiState by viewModel.uiState.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        BalanceSummaryCard(
                                title = "Income",
                                amount = "$${FormatUtils.formatAmount(uiState.totalIncome)}",
                                modifier = Modifier.weight(1f),
                                amountColor = GreenIncome
                        )
                        BalanceSummaryCard(
                                title = "Expenses",
                                amount = "$${FormatUtils.formatAmount(uiState.totalExpenses)}",
                                modifier = Modifier.weight(1f),
                                amountColor = RedExpense
                        )
                        BalanceSummaryCard(
                                title = "Net Growth",
                                amount = "$${FormatUtils.formatAmount(uiState.netGrowth)}",
                                modifier = Modifier.weight(1f),
                                containerColor = PrimaryPurple,
                                contentColor = Color.White
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
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
                                                onClick = onAddEntryClick,
                                                modifier = Modifier.weight(1f)
                                        ) {
                                                Icon(
                                                        Icons.Default.Add,
                                                        contentDescription = "Add new entry",
                                                        modifier = Modifier.padding(end = 8.dp)
                                                )
                                                Text("Add Entry")
                                        }
                                        OutlinedButton(
                                                onClick = onRemindersClick,
                                                modifier = Modifier.weight(1f),
                                        ) {
                                                Icon(
                                                        Icons.Default.Notifications,
                                                        contentDescription = "Check reminders"
                                                )
                                                Text("Reminders")
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Recent Activity", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) { items(uiState.transactions) { transaction -> TransactionItem(transaction) } }
        }
}
