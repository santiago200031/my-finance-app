package com.mobilecomputing.myfinance.ui.models

import com.mobilecomputing.myfinance.data.contract.ContractType
import java.util.Date

data class ContractUiModel(
    val id: String,
    val description: String,
    val amount: Double,
    val date: Date,
    val categoryId: String,
    val type: ContractType,
    val categoryName: String = "",
    val categoryIcon: String = "",
    val categoryColor: String = "",
    val formattedDate: String = ""
)
