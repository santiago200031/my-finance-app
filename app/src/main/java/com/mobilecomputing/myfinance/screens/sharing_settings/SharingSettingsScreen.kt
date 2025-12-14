package com.mobilecomputing.myfinance.screens.sharing_settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.contracts.ContractsViewModel
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun SharingSettingsScreen(
        viewModel: ContractsViewModel = viewModel(factory = AppViewModelProvider.Factory),
        onNavigateToSharedContracts: (String) -> Unit = {}
) {
        var emailInput by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                        "Sharing Settings",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Add Trusted Email Section
                OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Trusted Email") },
                        modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { emailInput = "" }, modifier = Modifier.align(Alignment.End)) {
                        Text("Add Trusted User")
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                        onClick = {
                                scope.launch {
                                        val userId =
                                                viewModel.resolveUserIdFromEmail(
                                                        "villavicencioandrs@gmail.com"
                                                )
                                        if (userId != null) {
                                                onNavigateToSharedContracts(userId)
                                        } else {}
                                }
                        },
                        modifier = Modifier.fillMaxWidth()
                ) { Text("View villavicencioandrs's Contracts") }
        }
}
