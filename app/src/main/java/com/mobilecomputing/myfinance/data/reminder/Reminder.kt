package com.mobilecomputing.myfinance.data.reminder

import java.time.LocalDate
import java.util.UUID

data class Reminder(
        val id: String = UUID.randomUUID().toString(),
        val contractId: String,
        val reminderDate: LocalDate,
        val isActive: Boolean = true
)
