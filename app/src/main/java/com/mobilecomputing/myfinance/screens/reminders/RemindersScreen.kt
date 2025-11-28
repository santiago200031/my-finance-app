package com.mobilecomputing.myfinance.screens.reminders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Reminder(val title: String, val date: String, val description: String)

@Composable
@Preview(showBackground = true)
fun RemindersScreen() {
    val reminders = listOf(
        Reminder("Pay Rent", "2023-11-01", "Monthly rent payment"),
        Reminder("Car Service", "2023-11-15", "Yearly maintenance"),
        Reminder("Mom's Birthday", "2023-11-20", "Buy a gift")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Reminders", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reminders) { reminder ->
                ReminderItem(reminder)
            }
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(reminder.title, style = MaterialTheme.typography.titleMedium)
            Text(reminder.date, style = MaterialTheme.typography.bodyMedium)
            Text(reminder.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}
