package com.mobilecomputing.myfinance.data.models.contract

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Contract(
    @DocumentId
    val id: String = "",

    val title: String = "",
    val amount: Double = 0.0,
    val provider: String = "",

    val startDate: Date = Date(),
    val endDate: Date? = null,

    val paymentCycle: PaymentCycle = PaymentCycle.MONTHLY,

    val reminderDaysBefore: Int = 30,
    val isReminderActive: Boolean = true
)
