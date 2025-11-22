package com.mobilecomputing.myfinance.screens.dashboard.data

import com.mobilecomputing.myfinance.data.models.transaction.Transaction
import com.mobilecomputing.myfinance.data.models.transaction.TransactionType
import java.util.Date
import java.util.Calendar

fun getMockTransactions(): List<Transaction> {
    val calendar = Calendar.getInstance()

    return listOf(
        Transaction(
            id = "1",
            userId = "mock_user",
            amount = 75.50,
            description = "Groceries",
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
            categoryId = "cat_food",
            categoryName = "Food",
            type = TransactionType.EXPENSE
        ),
        Transaction(
            id = "2",
            userId = "mock_user",
            amount = 2500.00,
            description = "Salary",
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -5) }.time,
            categoryId = "cat_income",
            categoryName = "Income",
            type = TransactionType.INCOME
        ),
        Transaction(
            id = "3",
            userId = "mock_user",
            amount = 1200.00,
            description = "Rent",
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            categoryId = "cat_housing",
            categoryName = "Housing",
            type = TransactionType.EXPENSE
        ),
        Transaction(
            id = "4",
            userId = "mock_user",
            amount = 4.25,
            description = "Coffee",
            date = Date(),
            categoryId = "cat_food",
            categoryName = "Food",
            type = TransactionType.EXPENSE
        ),
        Transaction(
            id = "5",
            userId = "mock_user",
            amount = 300.00,
            description = "Freelance Work",
            date = calendar.apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
            categoryId = "cat_income",
            categoryName = "Income",
            type = TransactionType.INCOME
        )
    )
}
