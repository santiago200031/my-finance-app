package com.mobilecomputing.myfinance.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.ui.AppViewModelProvider

@Composable
fun SettingsScreen(
        viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
        val uiState by viewModel.uiState.collectAsState()
        val scrollState = rememberScrollState()

        Scaffold { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(padding)
                                        .padding(16.dp)
                                        .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                        uiState.user?.let { user -> ProfileCard(user) }

                        AppearanceSection(
                                isDarkTheme = uiState.user?.settings?.isDarkTheme == true,
                                onThemeChange = viewModel::updateTheme
                        )

                        LocalizationSection(
                                language = uiState.user?.settings?.language ?: "en",
                                currency = uiState.user?.settings?.currency ?: "USD",
                                dateFormat = uiState.user?.settings?.dateFormat ?: "MM/DD/YYYY"
                        )

                        DataManagementSection()
                }
        }
}

@Composable
fun ProfileCard(user: User) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier =
                                        Modifier.size(60.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                        ) {
                                // Show initials if no image
                                val initials =
                                        (user.firstName.take(1) + user.lastName.take(1)).uppercase()
                                Text(
                                        text = initials,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                                Text(
                                        text = "${user.firstName} ${user.lastName}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        text = user.email,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                }
        }
}

@Composable
fun AppearanceSection(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
        Column {
                Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                )
                ) {
                        SettingsItem(
                                icon = Icons.Default.LightMode,
                                label = "Theme",
                                trailingContent = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                        text = if (isDarkTheme) "Dark" else "Light",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        modifier = Modifier.padding(end = 8.dp)
                                                )
                                                Switch(
                                                        checked = isDarkTheme,
                                                        onCheckedChange = { onThemeChange(it) }
                                                )
                                        }
                                }
                        )
                }
        }
}

@Composable
fun LocalizationSection(language: String, currency: String, dateFormat: String) {
        Column {
                Text(
                        text = "Localization",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                )
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

@Composable
fun DataManagementSection() {
        Column {
                Text(
                        text = "Data Management",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                )
                ) {
                        Column {
                                SettingsItem(icon = Icons.Default.Download, label = "Export Data")
                                CustomDivider()
                                SettingsItem(icon = Icons.Default.Share, label = "Sharing Settings")
                        }
                }
        }
}

@Composable
fun SettingsItem(
        icon: ImageVector,
        label: String,
        value: String? = null,
        trailingContent: @Composable (() -> Unit)? = null
) {
        Row(
                modifier = Modifier.fillMaxWidth().clickable { /* TODO */}.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                )

                if (trailingContent != null) {
                        trailingContent()
                } else if (value != null) {
                        Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                        )
                }
        }
}

@Composable
fun CustomDivider() {
        HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp
        )
}
