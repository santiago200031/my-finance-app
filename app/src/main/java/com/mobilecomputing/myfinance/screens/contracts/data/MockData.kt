package com.mobilecomputing.myfinance.screens.contracts.data

import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import java.util.Calendar

fun getMockContracts(): List<Contract> {
        val calendar = java.util.Calendar.getInstance()

        return listOf(
                Contract(
                        id = "0",
                        title = "Salary",
                        amount = 3500.0,
                        type = ContractType.INCOME,
                        provider = "Tech Corp",
                        paymentCycle = PaymentCycle.MONTHLY,
                        isReminderActive = false,
                        status = ContractStatus.ACTIVE,
                        autoRenewEnabled = true,
                        nextPaymentDate = calendar.apply { set(2025, 10, 30) }.time // Nov 30, 2025
                ),
                Contract(
                        id = "1",
                        title = "Netflix Subscription",
                        amount = 15.99,
                        type = ContractType.EXPENSE,
                        provider = "Netflix",
                        paymentCycle = PaymentCycle.MONTHLY,
                        isReminderActive = true,
                        status = ContractStatus.ACTIVE,
                        autoRenewEnabled = true,
                        nextPaymentDate = calendar.apply { set(2025, 11, 1) }.time // Dec 1, 2025
                ),
                Contract(
                        id = "2",
                        title = "Gym Membership",
                        amount = 49.99,
                        type = ContractType.EXPENSE,
                        provider = "FitGym",
                        paymentCycle = PaymentCycle.MONTHLY,
                        isReminderActive = true,
                        status = ContractStatus.ACTIVE,
                        autoRenewEnabled = true,
                        nextPaymentDate = calendar.apply { set(2025, 10, 15) }.time // Nov 15, 2025
                ),
                Contract(
                        id = "3",
                        title = "Car Insurance",
                        amount = 120.0,
                        type = ContractType.EXPENSE,
                        provider = "SafeDrive",
                        paymentCycle = PaymentCycle.MONTHLY,
                        isReminderActive = true,
                        status = ContractStatus.EXPIRING,
                        autoRenewEnabled = false,
                        nextPaymentDate = calendar.apply { set(2025, 10, 10) }.time, // Nov 10, 2025
                        endDate = calendar.apply { set(2025, 11, 31) }.time // Dec 31, 2025
                ),
                Contract(
                        id = "4",
                        title = "Mobile Plan",
                        amount = 35.0,
                        type = ContractType.EXPENSE,
                        provider = "SpeedyNet",
                        paymentCycle = PaymentCycle.MONTHLY,
                        isReminderActive = true,
                        status = ContractStatus.ACTIVE,
                        autoRenewEnabled = true,
                        nextPaymentDate = calendar.apply { set(2025, 10, 5) }.time // Nov 5, 2025
                )
        )
}
