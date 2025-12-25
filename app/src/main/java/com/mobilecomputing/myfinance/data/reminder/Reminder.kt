package com.mobilecomputing.myfinance.data.reminder

import com.google.firebase.firestore.DocumentId
import java.util.Date
import java.util.UUID

data class Reminder(
        @DocumentId
        val id: String = UUID.randomUUID().toString(),
        val contractId: String,
        val reminderDate: Date,
        val isActive: Boolean = true
)
