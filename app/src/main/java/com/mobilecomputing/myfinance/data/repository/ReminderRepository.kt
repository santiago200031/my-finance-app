package com.mobilecomputing.myfinance.data.repository

import com.mobilecomputing.myfinance.data.reminder.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun getReminderById(id: String): Flow<Reminder?>

    suspend fun addReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(id: String)
    suspend fun deleteRemindersForContract(contractId: String)
}
