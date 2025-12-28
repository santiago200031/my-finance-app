package com.mobilecomputing.myfinance.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onThemeChange: (Boolean) -> Unit,
    onSwitchUserClick: () -> Unit,
    onSharingSettingsClick: () -> Unit,
    onExportDataClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppConstants.PADDING_MEDIUM)
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_LARGE)
        ) {
            uiState.user?.let { user -> ProfileCard(user) }

            AppearanceSection(
                isDarkTheme = uiState.user?.settings?.isDarkTheme == true,
                onThemeChange = onThemeChange
            )

            LocalizationSection(
                language = uiState.user?.settings?.language ?: "en",
                currency = uiState.user?.settings?.currency ?: "USD",
                dateFormat = uiState.user?.settings?.dateFormat ?: "MM/DD/YYYY"
            )

            DataManagementSection(
                onSharingSettingsClick = onSharingSettingsClick,
                onSwitchUserClick = onSwitchUserClick,
                onExportDataClick = onExportDataClick
            )
        }
    }
}