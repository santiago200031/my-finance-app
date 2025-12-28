package com.mobilecomputing.myfinance.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.data.models.user.UserSettings
import com.mobilecomputing.myfinance.screens.settings.components.SettingsScreenContent
import com.mobilecomputing.myfinance.ui.AppViewModelProvider

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onSharingSettingsClick: () -> Unit = {},
    onExportDataClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreenContent(
        uiState = uiState,
        onThemeChange = viewModel::updateTheme,
        onSwitchUserClick = viewModel::switchUser,
        onSharingSettingsClick = onSharingSettingsClick,
        onExportDataClick = onExportDataClick
    )
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreenContent(
        uiState =
            SettingsUiState(
                user =
                    User(
                        firstName = "Santiago",
                        lastName = "Villavicencio",
                        email = "s-svilla@haw-landshut.de",
                        settings =
                            UserSettings(
                                isDarkTheme = true,
                                currency = "EUR",
                                dateFormat = "dd/MM/yyyy"
                            )
                    ),
                isLoading = false
            ),
        onThemeChange = {},
        onSwitchUserClick = {},
        onSharingSettingsClick = {},
        onExportDataClick = {}
    )
}
