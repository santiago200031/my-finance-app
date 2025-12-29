package com.mobilecomputing.myfinance.utils

import java.util.Locale

object FormatUtils {

    fun formatCurrency(amount: Double, currency: String = "EUR (€)"): String {
        return when {
            currency.contains("USD") || currency == "$" -> String.format(Locale.US, "$%.2f", amount)
            currency.contains("EUR") || currency == "€" ->
                String.format(Locale.GERMAN, "%.2f €", amount)

            currency.contains("CHF") -> String.format(Locale.GERMAN, "%.2f CHF", amount)
            else -> String.format(Locale.getDefault(), "%.2f %s", amount, currency)
        }
    }
}
