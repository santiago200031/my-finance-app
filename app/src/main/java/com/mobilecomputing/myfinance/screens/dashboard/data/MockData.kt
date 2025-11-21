package com.mobilecomputing.myfinance.screens.dashboard.data

data class Transaction(
    val name: String,
    val category: String,
    val amount: Double,
    val isExpense: Boolean
)

fun getMockTransactions(): List<Transaction> {
    return listOf(
        Transaction("Groceries", "Food", 75.50, true),
        Transaction("Salary", "Income", 2500.00, false),
        Transaction("Rent", "Housing", 1200.00, true),
        Transaction("Coffee", "Food", 4.25, true),
        Transaction("Freelance Work", "Income", 300.00, false)
    )
}
