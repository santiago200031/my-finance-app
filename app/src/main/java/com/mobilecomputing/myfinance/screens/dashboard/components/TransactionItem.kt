package com.mobilecomputing.myfinance.screens.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.screens.dashboard.data.Transaction

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(transaction.name, fontWeight = FontWeight.Bold)
                Text(
                    transaction.category,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = if (transaction.isExpense) {
                    "-$${transaction.amount}"
                } else {
                    "+$${transaction.amount}"
                },

                color = if (transaction.isExpense) {
                    Color.Red
                } else {
                    Color.Green
                },

                fontWeight = FontWeight.Bold
            )
        }
    }
}
