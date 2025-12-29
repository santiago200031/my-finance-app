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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.utils.AppConstants
import java.util.UUID

@Composable
fun AddEntryScreen(
    viewModel: AddEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit = {},
    entryId: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

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
        modifier =
            Modifier
                .fillMaxSize()
                .padding(AppConstants.PADDING_MEDIUM)
                .verticalScroll(rememberScrollState()),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
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

                HorizontalDivider()

                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(AppConstants.ICON_SIZE_SMALL)
                            )
                            Spacer(modifier = Modifier.width(AppConstants.PADDING_SMALL))
                            Text("Add New Category", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    onClick = {
                        expanded = false
                        showAddDialog = true
                    }
                )
            }
            // Transparent overlay to capture clicks for dropdown
            Box(modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true })
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
                        contentColor = MaterialTheme.colorScheme.error
                    )
            ) { Text("Delete Entry") }
        }
    }
    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, limit ->
                viewModel.addCategory(
                    Category(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        budgetLimit = limit,
                        iconKey = "default",
                        colorHex = AppConstants.DEFAULT_CATEGORY_COLOR
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var title by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("Budget Limit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limitValue = limit.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank()) {
                        onSave(title, limitValue)
                        onDismiss()
                    }
                },
                enabled = title.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
