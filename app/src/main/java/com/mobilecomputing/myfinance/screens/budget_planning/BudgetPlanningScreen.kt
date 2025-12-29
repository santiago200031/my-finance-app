package com.mobilecomputing.myfinance.screens.budget_planning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.service.BudgetOverview
import com.mobilecomputing.myfinance.data.service.CategoryBudgetStatus
import com.mobilecomputing.myfinance.screens.budget_planning.components.AddCategoryDialog
import com.mobilecomputing.myfinance.screens.budget_planning.components.CategoryBudgetCard
import com.mobilecomputing.myfinance.screens.budget_planning.components.EditBudgetDialog
import com.mobilecomputing.myfinance.screens.budget_planning.components.TotalBudgetCard
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.theme.BudgetActionColor
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is BudgetUiState.Success -> {
                BudgetContent(
                    overview = state.overview,
                    onEditClick = { showEditDialog = true },
                    onAddCategoryClick = { showAddDialog = true },
                    onDeleteCategoryClick = viewModel::deleteCategory
                )
            }

            is BudgetUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Error: ${state.message}") }
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
                        colorHex = AppConstants.DEFAULT_CATEGORY_COLOR
                    )
                )
            }
        )
    }
}

@Composable
fun BudgetContent(
    overview: BudgetOverview,
    onEditClick: () -> Unit,
    onAddCategoryClick: () -> Unit,
    onDeleteCategoryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_MEDIUM)
    ) {
        item { TotalBudgetCard(overview) }

        items(overview.categoryStatuses) { status ->
            CategoryBudgetCard(
                status = status,
                onDeleteClick = { onDeleteCategoryClick(status.category.id) }
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
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = BudgetActionColor
                        ),
                    shape =
                        RoundedCornerShape(
                            AppConstants.CORNER_RADIUS_BUTTON
                        )
                ) { Text("Edit Budget", color = Color.White, fontSize = 16.sp) }

                Button(
                    onClick = onAddCategoryClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple
                        ),
                    shape =
                        RoundedCornerShape(
                            AppConstants.CORNER_RADIUS_BUTTON
                        )
                ) { Text("Add Category", color = Color.White, fontSize = 16.sp) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetPlanningScreenPreview() {
    val dummyCategory =
        Category(
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
            onAddCategoryClick = {},
            onDeleteCategoryClick = {}
        )
    }
}
