package com.mobilecomputing.myfinance.screens.entries.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.data.FinanceFilter

@Composable
fun FilterButtons(
        selectedFilter: FinanceFilter,
        onFilterSelected: (FinanceFilter) -> Unit,
        modifier: Modifier = Modifier
) {
    Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FinanceFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            if (isSelected) {
                ElevatedButton(onClick = { onFilterSelected(filter) }) { Text(filter.name) }
            } else {
                OutlinedButton(onClick = { onFilterSelected(filter) }) { Text(filter.name) }
            }
        }
    }
}
