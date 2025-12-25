package com.mobilecomputing.myfinance.screens.entries.components

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
import androidx.compose.ui.text.font.FontWeight
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.ui.models.EntryUiModel
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.RedExpense
import com.mobilecomputing.myfinance.utils.AppConstants
import com.mobilecomputing.myfinance.utils.FormatUtils

@Composable
fun EntryItem(
    transaction: EntryUiModel,
    modifier: Modifier = Modifier,
) {
    Card(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(AppConstants.PADDING_MEDIUM)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(transaction.description, fontWeight = FontWeight.Bold)
                Text(transaction.categoryName, style = MaterialTheme.typography.bodySmall)
                Text(text = transaction.formattedDate, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text =
                    if (transaction.type == EntryType.EXPENSE
                    ) {
                        "-$${FormatUtils.formatUSAmount(transaction.amount)}"
                    } else {
                        "+$${FormatUtils.formatUSAmount(transaction.amount)}"
                    },
                color =
                    if (transaction.type == EntryType.EXPENSE
                    ) {
                        RedExpense
                    } else {
                        GreenIncome
                    },
                fontWeight = FontWeight.Bold
            )
        }
    }
}
