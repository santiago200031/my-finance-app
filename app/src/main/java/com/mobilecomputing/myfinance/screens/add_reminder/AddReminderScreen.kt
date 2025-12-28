package com.mobilecomputing.myfinance.screens.add_reminder

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.utils.AppConstants
import com.mobilecomputing.myfinance.utils.DateUtils
import com.mobilecomputing.myfinance.utils.NotificationHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    navigateBack: () -> Unit,
    viewModel: AddReminderViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(uiState.showNotification) {
        if (uiState.showNotification) {
            NotificationHandler.showNotification(
                context,
                "Reminder Set",
                "Reminder for ${uiState.selectedContract?.title} set on ${uiState.reminderDate}"
            )
            viewModel.resetSaveState()
            navigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Reminder") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(AppConstants.PADDING_MEDIUM)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_MEDIUM)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.selectedContract?.title ?: "Select Contract",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Contract") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    uiState.availableContracts.forEach { contract ->
                        DropdownMenuItem(
                            text = { Text(contract.title) },
                            onClick = {
                                viewModel.onContractSelect(contract)
                                expanded = false
                            }
                        )
                    }
                }
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true })
            }

            // Contract Details (Start/End Date)
            uiState.selectedContract?.let { contract ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Column(modifier = Modifier.padding(AppConstants.PADDING_MEDIUM)) {
                        Text(
                            text = "Contract Details",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = AppConstants.PADDING_SMALL)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Start Date", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    DateUtils.formatDate(contract.startDate),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("End Date", style = MaterialTheme.typography.labelSmall)
                                val endDateText =
                                    contract.endDate?.let { DateUtils.formatDate(it) }
                                        ?: "Indefinite"
                                Text(endDateText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // Date Selection
            OutlinedTextField(
                value = uiState.reminderDate,
                onValueChange = viewModel::onDateChange,
                label = { Text("Reminder Date (dd.MM.yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))

            Button(
                onClick = viewModel::saveReminder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState.selectedContract != null && uiState.reminderDate.isNotBlank()
            ) { Text("Set Reminder") }
        }
    }
}
