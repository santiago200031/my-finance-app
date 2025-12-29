package com.mobilecomputing.myfinance.ui.models

import com.mobilecomputing.myfinance.data.entry.EntryType
import java.util.Date

data class EntryUiModel(
    val id: String,
    val description: String,
    val amount: Double,
    val date: Date,
    val categoryId: String,
    val type: EntryType,
    val categoryName: String = "",
    val categoryIcon: String = "",
    val categoryColor: String = "",
    val formattedDate: String = "",
    val currency: String = "EUR (€)"
)
