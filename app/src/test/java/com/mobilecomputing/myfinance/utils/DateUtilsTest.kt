package com.mobilecomputing.myfinance.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    fun formatDate_localDate_returnsCorrectDisplayFormat() {
        val date = LocalDate.of(2023, 12, 25)
        // AppConstants.DATE_FORMAT_DISPLAY = "MMM d, yyyy" -> "Dec 25, 2023"
        val formatted = DateUtils.formatDate(date)
        assertEquals("Dec 25, 2023", formatted)
    }

    @Test
    fun formatInputDate_localDate_returnsCorrectInputFormat() {
        val date = LocalDate.of(2023, 1, 1)
        // AppConstants.DATE_FORMAT_INPUT = "dd.MM.yyyy" -> "01.01.2023"
        val formatted = DateUtils.formatInputDate(date)
        assertEquals("01.01.2023", formatted)
    }

    @Test
    fun parseInputDate_validString_returnsLocalDate() {
        val dateString = "01.01.2023"
        val date = DateUtils.parseInputDate(dateString)
        assertEquals(LocalDate.of(2023, 1, 1), date)
    }
}
