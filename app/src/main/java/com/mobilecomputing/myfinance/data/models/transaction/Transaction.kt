package com.mobilecomputing.myfinance.data.models.transaction

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Transaction(
    @DocumentId
    val id: String = "",

    val userId: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val date: Date = Date(),

    // Foreign key to a Category
    val categoryId: String = "",
    val categoryName: String = "",

    val type: TransactionType = TransactionType.EXPENSE
)
