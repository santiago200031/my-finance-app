package com.mobilecomputing.myfinance.screens.budget_planning

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.service.BudgetOverview
import com.mobilecomputing.myfinance.data.service.CategoryBudgetStatus
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.theme.BudgetActionColor
import com.mobilecomputing.myfinance.ui.theme.BudgetGreen
import com.mobilecomputing.myfinance.ui.theme.BudgetOrange
import com.mobilecomputing.myfinance.ui.theme.BudgetRed
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.utils.AppConstants
import java.util.UUID

@Composable
fun BudgetPlanningScreen(
    viewModel: BudgetPlanningViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppConstants.PADDING_MEDIUM)
    ) {
        when (val state = uiState) {
            is BudgetUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is BudgetUiState.Success -> {
                BudgetContent(
                    overview = state.overview,
                    onEditClick = { showEditDialog = true },
                    onAddCategoryClick = { showAddDialog = true }
                )
            }

            is BudgetUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}")
                }
            }
        }
    }

    if (showEditDialog && uiState is BudgetUiState.Success) {
        val overview = (uiState as BudgetUiState.Success).overview
        EditBudgetDialog(
            categories = overview.categoryStatuses.map { it.category },
            onDismiss = { showEditDialog = false },
            onSave = { categoryId, newLimit ->
                viewModel.updateBudgetLimit(categoryId, newLimit)
            }
        )
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
                        colorHex = "#FF00FF"
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

@Composable
fun BudgetContent(
    overview: BudgetOverview,
    onEditClick: () -> Unit,
    onAddCategoryClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_MEDIUM)
    ) {
        item { TotalBudgetCard(overview) }

        items(overview.categoryStatuses) { status ->
            CategoryBudgetCard(
                status = status
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEditClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BudgetActionColor),
                    shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_BUTTON)
                ) { Text("Edit Budget", color = Color.White, fontSize = 16.sp) }

                Button(
                    onClick = onAddCategoryClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_BUTTON)
                ) { Text("Add Category", color = Color.White, fontSize = 16.sp) }
            }
        }
    }
}

@Composable
fun TotalBudgetCard(overview: BudgetOverview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryPurple),
        shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_MEDIUM)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Monthly Budget", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$${overview.totalSpent.toInt()} / $${overview.totalBudget.toInt()}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${overview.percentUsed}% of budget used",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))
            LinearProgressIndicator(
                progress = { (overview.percentUsed / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
fun CategoryBudgetCard(
    status: CategoryBudgetStatus
) {
    val isOverBudget = status.spentAmount > status.category.budgetLimit
    val progressColor =
        when {
            status.percentUsed >= 100 -> BudgetRed
            status.percentUsed >= 80 -> BudgetOrange
            else -> BudgetGreen
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_SMALL)
    ) {
        Column(modifier = Modifier.padding(AppConstants.PADDING_MEDIUM)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(status.category.title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    if (isOverBudget) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Over Budget",
                            tint = BudgetRed,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (status.percentUsed >= 80) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Near Limit",
                            tint = BudgetOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "On Track",
                            tint = BudgetGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${status.percentUsed}%",
                        color = progressColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        if (isOverBudget)
                            "$-${(status.spentAmount - status.category.budgetLimit).toInt()} left"
                        else "$${status.remainingAmount.toInt()} left",
                        color = if (isOverBudget) BudgetRed else BudgetGreen,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$${status.spentAmount.toInt()} / $${status.category.budgetLimit.toInt()}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (status.percentUsed / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.1f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            if (isOverBudget) {
                Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = BudgetRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(AppConstants.PADDING_XSMALL))
                    Text(
                        "Over budget by $${(status.spentAmount - status.category.budgetLimit).toInt()}",
                        color = BudgetRed,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
fun BudgetPlanningScreenPreview() {
    val dummyCategory =
        com.mobilecomputing.myfinance.data.category.Category(
            id = "1",
            title = "Groceries",
            budgetLimit = 500.0,
            iconKey = "shopping_cart",
            colorHex = "#FF0000"
        )
    val dummyStatus =
        CategoryBudgetStatus(
            category = dummyCategory,
            spentAmount = 150.0,
            remainingAmount = 350.0,
            percentUsed = 30
        )
    val dummyOverview =
        BudgetOverview(
            totalBudget = 2000.0,
            totalSpent = 800.0,
            percentUsed = 40,
            categoryStatuses =
                listOf(
                    dummyStatus,
                    dummyStatus.copy(
                        category =
                            dummyCategory.copy(
                                id = "2",
                                title = "Transport"
                            ),
                        percentUsed = 80
                    ),
                    dummyStatus.copy(
                        category =
                            dummyCategory.copy(
                                id = "3",
                                title = "Entertainment"
                            ),
                        percentUsed = 100
                    )
                )
        )

    MaterialTheme {
        BudgetContent(
            overview = dummyOverview,
            onEditClick = {},
            onAddCategoryClick = {}
        )
    }
}
