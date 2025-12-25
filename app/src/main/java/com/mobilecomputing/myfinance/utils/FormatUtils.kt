package com.mobilecomputing.myfinance.utils

import java.util.Locale

object FormatUtils {

    fun formatCurrency(amount: Double): String {
        return String.format(Locale.getDefault(), "%.2f €", amount)
    }

    fun formatUSAmount(amount: Double): String {
        return String.format(Locale.US, "%.2f", amount)
    }

    fun formatEUAmount(amount: Double): String {
        return String.format(Locale.GERMAN, "%.2f", amount)
    }
}
