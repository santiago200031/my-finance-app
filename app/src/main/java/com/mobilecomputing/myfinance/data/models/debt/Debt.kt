package com.mobilecomputing.myfinance.data.models.debt

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Debt(
    @DocumentId
    val id: String = "",

    val creditor: String = "",
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,

    val repaymentRate: Double = 0.0,
    val dueDate: Date? = null,

    val status: DebtStatus = DebtStatus.OPEN
) {

    val remainingAmount: Double
        get() = totalAmount - paidAmount
}
