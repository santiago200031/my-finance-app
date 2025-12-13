package com.mobilecomputing.myfinance.data.category

import com.google.firebase.firestore.DocumentId
import com.mobilecomputing.myfinance.data.contract.ContractType

data class Category(
        @DocumentId val id: String = "",
        val title: String = "",
        val type: ContractType = ContractType.EXPENSE,
        val iconKey: String = "help", // Default icon
        val colorHex: String = "#000000",
        val isSystemDefault: Boolean = false
) {
        companion object {
                // Defaults to seed
                val defaultCategories =
                        listOf(
                                Category(
                                        id = "1",
                                        title = "Salary",
                                        type = ContractType.INCOME,
                                        colorHex = "#10B981",
                                        iconKey = "wallet",
                                        isSystemDefault = true
                                ),
                                Category(
                                        id = "2",
                                        title = "Freelance",
                                        type = ContractType.INCOME,
                                        colorHex = "#34D399",
                                        iconKey = "briefcase",
                                        isSystemDefault = true
                                ),
                                Category(
                                        id = "3",
                                        title = "Food",
                                        type = ContractType.EXPENSE,
                                        colorHex = "#EF4444",
                                        iconKey = "fast-food",
                                        isSystemDefault = true
                                ),
                                Category(
                                        id = "4",
                                        title = "Transport",
                                        type = ContractType.EXPENSE,
                                        colorHex = "#F59E0B",
                                        iconKey = "car",
                                        isSystemDefault = true
                                ),
                                Category(
                                        id = "5",
                                        title = "Entertainment",
                                        type = ContractType.EXPENSE,
                                        colorHex = "#8B5CF6",
                                        iconKey = "tv",
                                        isSystemDefault = true
                                ),
                                Category(
                                        id = "6",
                                        title = "Shopping",
                                        type = ContractType.EXPENSE,
                                        colorHex = "#EC4899",
                                        iconKey = "bag",
                                        isSystemDefault = true
                                ),
                                Category(
                                        id = "7",
                                        title = "Housing",
                                        type = ContractType.EXPENSE,
                                        colorHex = "#3B82F6",
                                        iconKey = "home",
                                        isSystemDefault = true
                                )
                        )
        }
}
