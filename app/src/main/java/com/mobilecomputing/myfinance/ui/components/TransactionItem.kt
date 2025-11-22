package com.mobilecomputing.myfinance.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.data.models.transaction.Transaction
import com.mobilecomputing.myfinance.data.models.transaction.TransactionType
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    showPadding: Boolean = true
) {
    Card(
        modifier = if (showPadding) {
            modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
        } else {
            modifier.fillMaxWidth()
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(transaction.description, fontWeight = FontWeight.Bold)
                Text(
                    transaction.categoryName,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(transaction.date),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = if (transaction.type == TransactionType.EXPENSE) {
                    "-$${transaction.amount}"
                } else {
                    "+$${transaction.amount}"
                },

                color = if (transaction.type == TransactionType.EXPENSE) {
                    Color.Red
                } else {
                    Color.Green
                },

                fontWeight = FontWeight.Bold
            )
        }
    }
}
