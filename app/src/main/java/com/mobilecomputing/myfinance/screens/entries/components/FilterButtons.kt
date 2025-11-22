package com.mobilecomputing.myfinance.screens.entries.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterButtons(
    selectedFilter: TransactionFilter,
    onFilterSelected: (TransactionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == TransactionFilter.ALL,
            onClick = { onFilterSelected(TransactionFilter.ALL) },
            label = { Text("All") }
        )
        FilterChip(
            selected = selectedFilter == TransactionFilter.INCOME,
            onClick = { onFilterSelected(TransactionFilter.INCOME) },
            label = { Text("Income") }
        )
        FilterChip(
            selected = selectedFilter == TransactionFilter.EXPENSE,
            onClick = { onFilterSelected(TransactionFilter.EXPENSE) },
            label = { Text("Expense") }
        )
    }
}
