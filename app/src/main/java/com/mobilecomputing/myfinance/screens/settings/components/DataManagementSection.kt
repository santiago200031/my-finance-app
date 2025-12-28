package com.mobilecomputing.myfinance.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun DataManagementSection(
    onSharingSettingsClick: () -> Unit,
    onSwitchUserClick: () -> Unit,
    onExportDataClick: () -> Unit
) {
    Column {
        Text(
            text = "Data Management",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = AppConstants.PADDING_SMALL)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                SettingsItem(
                    icon = Icons.Default.Download,
                    label = "Export Data",
                    onClick = onExportDataClick
                )
                CustomDivider()
                SettingsItem(
                    icon = Icons.Default.Share,
                    label = "Sharing Settings",
                    onClick = onSharingSettingsClick
                )
                CustomDivider()
                SettingsItem(
                    icon = Icons.Default.ImportExport,
                    label = "Switch User",
                    onClick = onSwitchUserClick
                )
            }
        }
    }
}
