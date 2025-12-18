package com.mobilecomputing.myfinance.screens.contracts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mobilecomputing.myfinance.data.contract.ContractFilter
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun ContractFilterButtons(
    selectedFilter: ContractFilter,
    onFilterSelected: (ContractFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = AppConstants.PADDING_MEDIUM),
            horizontalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)
    ) {
        ContractFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            if (isSelected) {
                ElevatedButton(onClick = { onFilterSelected(filter) }) { Text(filter.name) }
            } else {
                OutlinedButton(onClick = { onFilterSelected(filter) }) { Text(filter.name) }
            }
        }
    }
}
