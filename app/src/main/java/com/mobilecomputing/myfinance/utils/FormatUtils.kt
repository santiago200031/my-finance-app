package com.mobilecomputing.myfinance.utils

import java.util.Locale

object FormatUtils {

    fun formatCurrency(amount: Double): String {
        return String.format(Locale.getDefault(), "%.2f €", amount)
    }

    fun formatAmount(amount: Double): String {
        return String.format(Locale.US, "%.2f", amount)
    }
}
