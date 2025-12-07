package com.mobilecomputing.myfinance.data.services

import com.mobilecomputing.myfinance.data.models.contract.Contract
import com.mobilecomputing.myfinance.data.models.contract.ContractStatus
import com.mobilecomputing.myfinance.data.models.contract.ContractType
import com.mobilecomputing.myfinance.data.models.transaction.Transaction
import com.mobilecomputing.myfinance.data.models.transaction.TransactionType

class FinanceService {

    fun calculateTotalIncome(transactions: List<Transaction>): Double {
        var total = 0.0
        for (transaction in transactions) {
            if (transaction.type == TransactionType.INCOME) {
                total += transaction.amount
            }
        }
        return total
    }

    fun calculateTotalExpenses(transactions: List<Transaction>): Double {
        var total = 0.0
        for (transaction in transactions) {
            if (transaction.type == TransactionType.EXPENSE) {
                total += transaction.amount
            }
        }
        return total
    }

    fun calculateNetGrowth(transactions: List<Transaction>): Double {
        val income = calculateTotalIncome(transactions)
        val expenses = calculateTotalExpenses(transactions)
        return income - expenses
    }

    fun calculateMonthlyIncome(contracts: List<Contract>): Double {
        var total = 0.0
        for (contract in contracts) {
            if (contract.type == ContractType.INCOME) {
                total += contract.amount
            }
        }
        return total
    }

    fun calculateMonthlyExpenses(contracts: List<Contract>): Double {
        var total = 0.0
        for (contract in contracts) {
            if (contract.type == ContractType.EXPENSE) {
                total += contract.amount
            }
        }
        return total
    }

    fun calculateNetMonthly(contracts: List<Contract>): Double {
        val income = calculateMonthlyIncome(contracts)
        val expenses = calculateMonthlyExpenses(contracts)
        return income - expenses
    }

    fun getActiveContractsCount(contracts: List<Contract>): Int {
        var count = 0
        for (contract in contracts) {
            if (contract.status == ContractStatus.ACTIVE) {
                count++
            }
        }
        return count
    }

    fun getExpiringContractsCount(contracts: List<Contract>): Int {
        var count = 0
        for (contract in contracts) {
            if (contract.status == ContractStatus.EXPIRING) {
                count++
            }
        }
        return count
    }
}
