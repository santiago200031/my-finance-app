package com.mobilecomputing.myfinance.screens.add_entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun AddEntryScreen(
        viewModel: AddEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
        navigateBack: () -> Unit = {},
        entryId: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            viewModel.loadEntry(entryId)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.resetSaveState()
            navigateBack()
        }
    }

    Column(
            modifier = Modifier.fillMaxSize().padding(AppConstants.PADDING_MEDIUM).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_MEDIUM)
    ) {
        Text(
                text = if (entryId != null) "Edit Entry" else "Add New Entry",
                style = MaterialTheme.typography.headlineMedium
        )

        // Type Selection (Income / Expense)
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)
        ) {
            EntryType.entries.forEach { type ->
                val isSelected = uiState.selectedType == type
                if (isSelected) {
                    Button(
                            onClick = { viewModel.onTypeSelect(type) },
                            modifier = Modifier.weight(1f)
                    ) { Text(type.name) }
                } else {
                    OutlinedButton(
                            onClick = { viewModel.onTypeSelect(type) },
                            modifier = Modifier.weight(1f)
                    ) { Text(type.name) }
                }
            }
        }

        // Amount Input
        OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
        )

        // Category Selection
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                    value = uiState.selectedCategory?.title ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    },
                    modifier = Modifier.fillMaxWidth().clickable { expanded = true }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                uiState.categories.forEach { category ->
                    DropdownMenuItem(
                            text = { Text(category.title) },
                            onClick = {
                                viewModel.onCategorySelect(category)
                                expanded = false
                            }
                    )
                }
            }
            // Transparent overlay to capture clicks for dropdown
            Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
        }

        // Description Input
        OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))

        // Save Button
        Button(
                onClick = viewModel::saveEntry,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.amount.isNotBlank() && uiState.selectedCategory != null
        ) { Text("Save Entry") }

        if (uiState.entryId != null) {
            OutlinedButton(
                    onClick = viewModel::deleteEntry,
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            ButtonDefaults.outlinedButtonColors(
                                    contentColor =
                                            MaterialTheme.colorScheme
                                                    .error
                            )
            ) { Text("Delete Entry") }
        }
    }
}
