package com.mobilecomputing.myfinance.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun AppearanceSection(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    Column {
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = AppConstants.PADDING_SMALL)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            SettingsItem(
                icon = Icons.Default.LightMode,
                label = "Theme",
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isDarkTheme) "Dark" else "Light",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = AppConstants.PADDING_SMALL)
                        )
                        Switch(checked = isDarkTheme, onCheckedChange = { onThemeChange(it) })
                    }
                }
            )
        }
    }
}
