package com.mobilecomputing.myfinance.screens.entries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.entries.components.FilterButtons
import com.mobilecomputing.myfinance.screens.entries.components.TransactionItem
import com.mobilecomputing.myfinance.ui.AppViewModelProvider

@Composable
fun EntriesScreen(
        viewModel: EntriesViewModel = viewModel(factory = AppViewModelProvider.Factory),
        onAddEntryClick: () -> Unit = {},
        onEntryClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = onAddEntryClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Entry")
                }
            }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            FilterButtons(
                    selectedFilter = uiState.filter,
                    onFilterSelected = viewModel::onFilterChanged
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(uiState.transactions) { transaction ->
                    TransactionItem(
                            transaction = transaction,
                            modifier = Modifier.clickable { onEntryClick(transaction.id) }
                    )
                }
            }
        }
    }
}
