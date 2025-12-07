package com.mobilecomputing.myfinance.screens.add_entry

import com.mobilecomputing.myfinance.data.models.category.Category
import com.mobilecomputing.myfinance.data.models.contract.PaymentCycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AddEntryUiState(
    val title: String = "",
    val amount: String = "",
    val type: EntryType = EntryType.EXPENSE,
    val category: Category? = null,
    val date: String = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()),
    val description: String = "",
    val provider: String = "",
    val paymentCycle: PaymentCycle = PaymentCycle.MONTHLY
)
