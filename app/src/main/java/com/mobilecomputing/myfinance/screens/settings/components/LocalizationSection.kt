package com.mobilecomputing.myfinance.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun LocalizationSection(language: String, currency: String, dateFormat: String) {
    Column {
        Text(
            text = "Localization",
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
                    icon = Icons.Default.Language,
                    label = "Language",
                    value = if (language == "en") "English" else language
                )
                CustomDivider()
                SettingsItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Currency",
                    value = "$currency ($)"
                )
                CustomDivider()
                SettingsItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Date Format",
                    value = dateFormat
                )
            }
        }
    }
}
