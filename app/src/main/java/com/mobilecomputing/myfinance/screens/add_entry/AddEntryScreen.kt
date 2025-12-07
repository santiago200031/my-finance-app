package com.mobilecomputing.myfinance.screens.add_entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.ui.theme.MyFinanceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(viewModel: AddEntryViewModel = viewModel()) {
    val uiState = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "New Entry",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = uiState.title,
            onValueChange = viewModel::onTitleChanged,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.amount,
            onValueChange = viewModel::onAmountChanged,
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Text("Type", style = MaterialTheme.typography.labelLarge)
        val entryTypes = EntryType.entries
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(entryTypes[0], entryTypes[2]).forEach { entryType ->
                    val isSelected = uiState.type == entryType
                    val modifier = Modifier.fillMaxWidth()
                    if (isSelected) {
                        Button(
                            onClick = { viewModel.onTypeChanged(entryType) },
                            modifier = modifier
                        ) {
                            Text(entryType.name, maxLines = 1)
                        }
                    } else {
                        TextButton(
                            onClick = { viewModel.onTypeChanged(entryType) },
                            modifier = modifier
                        ) {
                            Text(entryType.name, maxLines = 1)
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(entryTypes[1], entryTypes[3]).forEach { entryType ->
                    val isSelected = uiState.type == entryType
                    val modifier = Modifier.fillMaxWidth()
                    if (isSelected) {
                        Button(
                            onClick = { viewModel.onTypeChanged(entryType) },
                            modifier = modifier
                        ) {
                            Text(entryType.name, maxLines = 1)
                        }
                    } else {
                        TextButton(
                            onClick = { viewModel.onTypeChanged(entryType) },
                            modifier = modifier
                        ) {
                            Text(entryType.name, maxLines = 1)
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = uiState.category?.name ?: "",
            onValueChange = viewModel::onCategoryChanged,
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = false
        )

        OutlinedTextField(
            value = uiState.date,
            onValueChange = viewModel::onDateChanged,
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("dd.MM.yyyy") }
        )

        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChanged,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = viewModel::onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEntryScreenPreview() {
    MyFinanceTheme {
        AddEntryScreen()
    }
}
