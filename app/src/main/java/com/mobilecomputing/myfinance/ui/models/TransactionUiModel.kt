package com.mobilecomputing.myfinance.ui.models

import com.mobilecomputing.myfinance.data.contract.ContractType
import java.time.LocalDateTime

data class TransactionUiModel(
        val id: String,
        val description: String,
        val amount: Double,
        val date: LocalDateTime,
        val categoryId: String,
        val type: ContractType,
        val categoryName: String = "",
        val categoryIcon: String = "",
        val categoryColor: String = "",
        val formattedDate: String = ""
)
