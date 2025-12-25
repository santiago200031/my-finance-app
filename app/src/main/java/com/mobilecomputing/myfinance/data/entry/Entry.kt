package com.mobilecomputing.myfinance.data.entry

import java.util.Date
import java.util.UUID

data class Entry(
        val id: String = UUID.randomUUID().toString(),
        val userId: String = "",
        val categoryId: String = "",
        val amount: Double = 0.0,
        val date: Date = Date(),
        val description: String? = null,
        val type: EntryType = EntryType.EXPENSE
)
