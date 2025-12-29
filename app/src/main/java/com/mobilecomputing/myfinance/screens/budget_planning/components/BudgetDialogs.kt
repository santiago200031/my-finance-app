package com.mobilecomputing.myfinance.screens.budget_planning.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.utils.AppConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onSave: (String, Double) -> Unit) {
    var title by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }

    AlertDialog(
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
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
    var budgetValue by remember { mutableStateOf(selectedCategory?.budgetLimit?.toString() ?: "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Budget Amounts") },
        text = {
            Column {
                Text("Select Category", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))

                categories.forEach { category ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppConstants.PADDING_XSMALL),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory?.id == category.id,
                            onClick = {
                                selectedCategory = category
                                budgetValue = category.budgetLimit.toString()
                            }
                        )
                        Text(category.title)
                    }
                }

                Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))
                OutlinedTextField(
                    value = budgetValue,
                    onValueChange = { budgetValue = it },
                    label = { Text("Budget Limit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val value = budgetValue.toDoubleOrNull() ?: 0.0
                    selectedCategory?.let { onSave(it.id, value) }
                    onDismiss()
                }
            ) { Text("Save") }
        },
    )
}
