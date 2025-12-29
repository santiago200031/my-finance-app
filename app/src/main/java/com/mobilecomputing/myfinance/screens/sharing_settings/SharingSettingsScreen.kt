package com.mobilecomputing.myfinance.screens.sharing_settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.contracts.ContractsViewModel
import com.mobilecomputing.myfinance.screens.settings.SettingsViewModel
import com.mobilecomputing.myfinance.screens.sharing_settings.components.SharingSettingsContent
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun SharingSettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contractViewModel: ContractsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToSharedContracts: (String) -> Unit = {}
) {
    var emailInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val currentUser = uiState.user

    SharingSettingsContent(
        emailInput = emailInput,
        onEmailInputChange = { emailInput = it },
        onAddTrustedEmail = {
            if (emailInput.isNotBlank()) {
                viewModel.addTrustedEmail(emailInput)
                emailInput = ""
            }
        },
        onRemoveTrustedEmail = { email -> viewModel.removeTrustedEmail(email) },
        currentUser = currentUser,
        onViewContracts = { email ->
            scope.launch {
                val userId = contractViewModel.resolveUserIdFromEmail(email)
                if (userId != null) {
                    onNavigateToSharedContracts(userId)
                }
            }
        }
    )
}
