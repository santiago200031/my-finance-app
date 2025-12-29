package com.mobilecomputing.myfinance.screens.settings.components

// Imports added automatically by the IDE usually, but we need to ensure we have valid imports
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun LocalizationSection(
    language: String,
    currency: String,
    dateFormat: String,
    onCurrencyChange: (String) -> Unit,
    onDateFormatChange: (String) -> Unit
) {
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showDateFormatDialog by remember { mutableStateOf(false) }

    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text(text = "Select Currency") },
            text = {
                Column {
                    val currencies = listOf("USD ($)", "EUR (€)", "CHF")
                    currencies.forEach { cur ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCurrencyChange(cur)
                                        showCurrencyDialog = false
                                    }
                                    .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = (cur == currency),
                                onClick = {
                                    onCurrencyChange(cur)
                                    showCurrencyDialog = false
                                }
                            )
                            Text(
                                text = cur,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDateFormatDialog) {
        AlertDialog(
            onDismissRequest = { showDateFormatDialog = false },
            title = { Text(text = "Select Date Format") },
            text = {
                Column {
                    AppConstants.DateFormatOption.all.forEach { format ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onDateFormatChange(format)
                                        showDateFormatDialog = false
                                    }
                                    .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = (format == dateFormat),
                                onClick = {
                                    onDateFormatChange(format)
                                    showDateFormatDialog = false
                                }
                            )
                            Text(
                                text = format,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDateFormatDialog = false }) { Text("Cancel") }
            }
        )
    }

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
                    value = currency,
                    onClick = { showCurrencyDialog = true }
                )
                CustomDivider()
                SettingsItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Date Format",
                    value = dateFormat,
                    onClick = { showDateFormatDialog = true }
                )
            }
        }
    }
}
