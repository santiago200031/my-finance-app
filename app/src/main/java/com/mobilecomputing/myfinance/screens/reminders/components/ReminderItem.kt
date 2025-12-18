package com.mobilecomputing.myfinance.screens.reminders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.data.reminder.Reminder
import com.mobilecomputing.myfinance.screens.reminders.ReminderUiItem
import com.mobilecomputing.myfinance.utils.AppConstants
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.ui.theme.SecondaryPurple
import com.mobilecomputing.myfinance.utils.DateUtils
import com.mobilecomputing.myfinance.utils.FormatUtils
import java.time.LocalDate

@Composable
fun ReminderItem(item: ReminderUiItem, onDeleteClick: () -> Unit) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
                Column(modifier = Modifier.padding(AppConstants.PADDING_MEDIUM)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = item.contractTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = onDeleteClick) {
                                        Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete Reminder",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }

                        Text(
                                text =
                                        "${FormatUtils.formatUSAmount(item.contractAmount)} due in ${item.daysUntil} days",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        Icons.Default.Alarm,
                                        contentDescription = null,
                                        modifier = Modifier.width(AppConstants.PADDING_MEDIUM),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                        text = DateUtils.formatDate(item.reminder.reminderDate),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))

                        Box(
                                modifier =
                                        Modifier.background(
                                                        color = SecondaryPurple,
                                                        shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_MEDIUM)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                                Text(
                                        "Contract",
                                        color = PrimaryPurple,
                                        style = MaterialTheme.typography.labelSmall
                                )
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun ReminderItemPreview() {
        val dummyReminder = Reminder(contractId = "123", reminderDate = LocalDate.now().plusDays(3))
        val dummyItem =
                ReminderUiItem(
                        reminder = dummyReminder,
                        contractTitle = "Gym Membership",
                        contractAmount = 45.0,
                        daysUntil = 3
                )
        ReminderItem(item = dummyItem, onDeleteClick = {})
}
