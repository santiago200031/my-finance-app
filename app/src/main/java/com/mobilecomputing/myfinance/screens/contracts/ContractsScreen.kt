package com.mobilecomputing.myfinance.screens.contracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractFilter
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.screens.contracts.components.ContractFilterButtons
import com.mobilecomputing.myfinance.screens.contracts.components.ContractItem
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.components.MonthYearSelector
import com.mobilecomputing.myfinance.ui.components.SummaryCardsRow
import com.mobilecomputing.myfinance.utils.AppConstants
import java.util.Date

@Composable
fun ContractsScreen(
    viewModel: ContractsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onAddContractClick: () -> Unit = {},
    onContractClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshContractStatuses() }

    ContractsScreenContent(
        uiState = uiState,
        onAddContractClick = onAddContractClick,
        onContractClick = onContractClick,
        onFilterSelected = viewModel::onFilterChanged,
        onPreviousMonth = { viewModel.updateMonth(-1) },
        onNextMonth = { viewModel.updateMonth(1) }
    )
}

@Composable
fun ContractsScreenContent(
    uiState: ContractsUiState,
    onAddContractClick: () -> Unit = {},
    onContractClick: (String) -> Unit = {},
    onFilterSelected: (ContractFilter) -> Unit = {},
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddContractClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Contract")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            // Summary Dashboard
            MonthYearSelector(
                currentDate = uiState.selectedDate,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            SummaryCardsRow(
                activeCount = uiState.activeCount,
                expiringCount = uiState.expiringCount,
                monthlyNetValue = uiState.monthlyNetValue
            )

            Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))

            ContractFilterButtons(
                selectedFilter = uiState.filter,
                onFilterSelected = onFilterSelected
            )

            Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(uiState.contracts) { contract ->
                    ContractItem(contract, onContractClick = { onContractClick(contract.id) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContractsScreenPreview() {
    val dummyState =
        ContractsUiState(
            contracts =
                listOf(
                    Contract(
                        id = "1",
                        title = "Netflix",
                        amount = 12.99,
                        paymentCycle = PaymentCycle.MONTHLY,
                        type = ContractType.EXPENSE,
                        startDate = Date(),
                        status = ContractStatus.ACTIVE,
                        nextPaymentDate = Date(),
                        totalAmount = null
                    )
                ),
            activeCount = 1,
            expiringCount = 0,
            monthlyNetValue = -12.99
        )
    ContractsScreenContent(uiState = dummyState)
}
