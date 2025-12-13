package com.mobilecomputing.myfinance.data.repository.impl

import com.mobilecomputing.myfinance.data.reminder.Reminder
import com.mobilecomputing.myfinance.data.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeReminderRepository : ReminderRepository {
    private val reminders = MutableStateFlow<List<Reminder>>(listOf())

    init {
        // Seed some data if needed, or leave empty
    }

    override fun getAllReminders(): Flow<List<Reminder>> = reminders

    override fun getReminderById(id: String): Flow<Reminder?> {
        return reminders.map { list -> list.find { it.id == id } }
    }

    override suspend fun addReminder(reminder: Reminder) {
        val current = reminders.value.toMutableList()
        current.add(reminder)
        reminders.value = current
    }

    override suspend fun updateReminder(reminder: Reminder) {
        val current = reminders.value.toMutableList()
        val index = current.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            current[index] = reminder
            reminders.value = current
        }
    }

    override suspend fun deleteReminder(id: String) {
        val current = reminders.value.toMutableList()
        current.removeAll { it.id == id }
        reminders.value = current
    }

    override suspend fun deleteRemindersForContract(contractId: String) {
        val current = reminders.value.toMutableList()
        current.removeAll { it.contractId == contractId }
        reminders.value = current
    }
}
