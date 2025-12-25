package com.mobilecomputing.myfinance.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatUtilsTest {

    @Test
    fun formatUSAmount_returnsDotSeparator() {
        val amount = 1234.56
        val formatted = FormatUtils.formatUSAmount(amount)
        assertEquals("1234.56", formatted)
    }

    @Test
    fun formatEUAmount_returnsCommaSeparator() {
        val amount = 1234.56
        val formatted = FormatUtils.formatEUAmount(amount)
        assertEquals("1234,56", formatted)
    }
}
