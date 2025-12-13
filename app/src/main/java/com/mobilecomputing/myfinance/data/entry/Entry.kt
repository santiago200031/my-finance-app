package com.mobilecomputing.myfinance.data.entry

import com.mobilecomputing.myfinance.data.contract.ContractType
import java.time.LocalDateTime
import java.util.UUID

data class Entry(
        val id: String = UUID.randomUUID().toString(),
        val userId: String = "",
        val categoryId: String = "",
        val amount: Double = 0.0,
        val date: LocalDateTime = LocalDateTime.now(),
        val description: String? = null,
        val type: ContractType = ContractType.EXPENSE
)
