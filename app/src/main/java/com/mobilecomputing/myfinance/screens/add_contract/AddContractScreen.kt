package com.mobilecomputing.myfinance.screens.add_contract

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun AddContractScreen(
        viewModel: AddContractViewModel = viewModel(factory = AppViewModelProvider.Factory),
        navigateBack: () -> Unit = {},
        contractId: String? = null
) {
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(contractId) {
                if (contractId != null) {
                        viewModel.loadContract(contractId)
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
                        Modifier.fillMaxSize().padding(AppConstants.PADDING_MEDIUM).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_MEDIUM)
        ) {
                Text(
                        text = if (contractId != null) "Edit Contract" else "Add New Contract",
                        style = MaterialTheme.typography.headlineMedium
                )

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)
                ) {
                        ContractType.entries.forEach { type ->
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

                OutlinedTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChange,
                        label = { Text("Contract Title") },
                        modifier = Modifier.fillMaxWidth()
                )

                // Installment / Monthly Payment
                OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = viewModel::onAmountChange,
                        label = {
                                Text(
                                        if (uiState.selectedType == ContractType.DEBT)
                                                "Monthly/Period Payment"
                                        else "Amount"
                                )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                )

                // Total Debt Amount (Only for Debt)
                if (uiState.selectedType == ContractType.DEBT) {
                        OutlinedTextField(
                                value = uiState.totalAmount.orEmpty(),
                                onValueChange = viewModel::onTotalAmountChange,
                                label = { Text("Total Debt Amount") },
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                        )
                }

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)
                ) {
                        OutlinedTextField(
                                value = uiState.startDate,
                                onValueChange = viewModel::onStartDateChange,
                                label = { Text("Start Date (dd.MM.yyyy)") },
                                modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                                value = uiState.expirationDate,
                                onValueChange = viewModel::onExpirationDateChange,
                                label = { Text("End Date (Opt.)") },
                                modifier = Modifier.weight(1f)
                        )
                }

                Text("Billing Cycle", style = MaterialTheme.typography.titleMedium)
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)
                ) {
                        PaymentCycle.entries.forEach { cycle ->
                                val isSelected = uiState.billingCycle == cycle
                                if (isSelected) {
                                        Button(
                                                onClick = { viewModel.onCycleSelect(cycle) },
                                                modifier = Modifier.weight(1f)
                                        ) { Text(cycle.name) }
                                } else {
                                        OutlinedButton(
                                                onClick = { viewModel.onCycleSelect(cycle) },
                                                modifier = Modifier.weight(1f)
                                        ) { Text(cycle.name) }
                                }
                        }
                }

                // Auto Renew
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text("Auto-Renew", style = MaterialTheme.typography.titleMedium)
                        androidx.compose.material3.Switch(
                                checked = uiState.isAutoRenew,
                                onCheckedChange = viewModel::onAutoRenewChange
                        )
                }

                // Status
                Text("Status", style = MaterialTheme.typography.titleMedium)
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)
                ) {
                        ContractStatus.entries.forEach { status ->
                                val isSelected = uiState.status == status
                                if (isSelected) {
                                        Button(
                                                onClick = { viewModel.onStatusChange(status) },
                                                modifier = Modifier.weight(1f)
                                        ) {
                                                Text(
                                                        status.name,
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        }
                                } else {
                                        OutlinedButton(
                                                onClick = { viewModel.onStatusChange(status) },
                                                modifier = Modifier.weight(1f)
                                        ) {
                                                Text(
                                                        status.name,
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))

                Button(
                        onClick = viewModel::saveContract,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.title.isNotBlank() && uiState.amount.isNotBlank()
                ) { Text("Save Contract") }

                if (uiState.contractId != null) {
                        OutlinedButton(
                                onClick = viewModel::deleteContract,
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                        )
                        ) { Text("Delete Contract") }
                }
        }
}
