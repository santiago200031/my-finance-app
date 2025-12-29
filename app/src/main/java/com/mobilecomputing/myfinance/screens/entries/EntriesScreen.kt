package com.mobilecomputing.myfinance.screens.entries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.entries.components.EntryFilterButtons
import com.mobilecomputing.myfinance.screens.entries.components.EntryItem
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.components.MonthYearSelector
import com.mobilecomputing.myfinance.utils.AppConstants

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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            EntryFilterButtons(
                selectedFilter = uiState.filter,
                onFilterSelected = viewModel::onFilterChanged
            )

            Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))

            // Category Filter
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = AppConstants.PADDING_MEDIUM),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val selectedCategory = uiState.selectedCategory

                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { viewModel.onCategorySelected(null) },
                    label = { Text("All Categories") }
                )

                uiState.categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory?.id == category.id,
                        onClick = {
                            if (selectedCategory?.id == category.id) {
                                viewModel.onCategorySelected(null)
                            } else {
                                viewModel.onCategorySelected(category)
                            }
                        },
                        label = { Text(category.title) }
                    )
                }
            }

            MonthYearSelector(
                currentDate = uiState.selectedDate,
                onPreviousMonth = { viewModel.updateMonth(-1) },
                onNextMonth = { viewModel.updateMonth(1) }
            )

            Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_XSMALL)
            ) {
                items(uiState.transactions) { transaction ->
                    EntryItem(
                        transaction = transaction,
                        modifier = Modifier.clickable { onEntryClick(transaction.id) }
                    )
                }
            }
        }
    }
}
