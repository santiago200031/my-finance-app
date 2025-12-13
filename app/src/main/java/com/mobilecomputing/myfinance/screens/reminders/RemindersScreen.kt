package com.mobilecomputing.myfinance.screens.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.reminders.components.ReminderItem
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun RemindersScreen(
        viewModel: RemindersViewModel = viewModel(factory = AppViewModelProvider.Factory),
        onAddReminderClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    RemindersScreenContent(
            uiState = uiState,
            onAddReminderClick = onAddReminderClick,
            onDeleteReminder = viewModel::deleteReminder
    )
}

@Composable
fun RemindersScreenContent(
        uiState: RemindersUiState,
        onAddReminderClick: () -> Unit = {},
        onDeleteReminder: (String) -> Unit = {}
) {
    Scaffold() { padding ->
        Column(
                modifier =
                        Modifier.fillMaxSize().padding(padding).padding(AppConstants.PADDING_MEDIUM)
        ) {
            // Header Card
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_MEDIUM)
            ) {
                Column(modifier = Modifier.padding(AppConstants.PADDING_MEDIUM)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                "Active Reminders",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = "${uiState.activeCount}",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val nextAlertText =
                            if (uiState.nextAlertInDays != null) {
                                "Next alert in ${uiState.nextAlertInDays} day(s)"
                            } else {
                                "No upcoming alerts"
                            }
                    Text(nextAlertText, color = Color.White.copy(alpha = 0.8f))
                }
            }

            Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))

            LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.reminders) { item ->
                    ReminderItem(
                            item = item,
                            onDeleteClick = { onDeleteReminder(item.reminder.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))

            Button(
                    onClick = onAddReminderClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors =
                            androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = PrimaryPurple
                            )
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Reminder")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemindersScreenPreview() {
    RemindersScreenContent(uiState = RemindersUiState(activeCount = 3, nextAlertInDays = 2))
}
