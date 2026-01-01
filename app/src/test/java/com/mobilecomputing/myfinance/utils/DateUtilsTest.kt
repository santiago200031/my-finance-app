package com.mobilecomputing.myfinance.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun `isSameMonth returns true for same month and year`() {
        val cal1 = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 15) }
        val cal2 = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 20) }
        assertTrue(DateUtils.isSameMonth(cal1.time, cal2.time))
    }

    @Test
    fun `isSameMonth returns false for different month`() {
        val cal1 = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 15) }
        val cal2 = Calendar.getInstance().apply { set(2024, Calendar.FEBRUARY, 15) }
        assertFalse(DateUtils.isSameMonth(cal1.time, cal2.time))
    }

    @Test
    fun `isSameMonth returns false for different year`() {
        val cal1 = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 15) }
        val cal2 = Calendar.getInstance().apply { set(2025, Calendar.JANUARY, 15) }
        assertFalse(DateUtils.isSameMonth(cal1.time, cal2.time))
    }

    @Test
    fun `addMonths adds months correctly`() {
        val cal = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 15) }
        val newDate = DateUtils.addMonths(cal.time, 1)
        val newCal = Calendar.getInstance().apply { time = newDate }

        assertEquals(Calendar.FEBRUARY, newCal.get(Calendar.MONTH))
        assertEquals(2024, newCal.get(Calendar.YEAR))
    }

    @Test
    fun `addMonths handles year transition`() {
        val cal = Calendar.getInstance().apply { set(2024, Calendar.DECEMBER, 15) }
        val newDate = DateUtils.addMonths(cal.time, 1)
        val newCal = Calendar.getInstance().apply { time = newDate }

        assertEquals(Calendar.JANUARY, newCal.get(Calendar.MONTH))
        assertEquals(2025, newCal.get(Calendar.YEAR))
    }

    @Test
    fun `addMonths handles subtraction`() {
        val cal = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 15) }
        val newDate = DateUtils.addMonths(cal.time, -1)
        val newCal = Calendar.getInstance().apply { time = newDate }

        assertEquals(Calendar.DECEMBER, newCal.get(Calendar.MONTH))
        assertEquals(2023, newCal.get(Calendar.YEAR))
    }

    @Test
    fun `formatMonthYear formats correctly`() {
        val cal = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 15) }

        val formatted = DateUtils.formatMonthYear(cal.time)

        assertEquals("January 2024", formatted)
    }

    @Test
    fun `formatDate uses provided pattern`() {
        val date = LocalDate.of(2024, 1, 15)
        val formatted = DateUtils.formatDate(date, "MM/dd/yyyy")
        assertEquals("01/15/2024", formatted)
    }

    @Test
    fun `parseInputDate parses correctly with provided pattern`() {
        val dateStr = "01/15/2024"
        val date = DateUtils.parseInputDate(dateStr, "MM/dd/yyyy")
        assertEquals(LocalDate.of(2024, 1, 15), date)
    }

    @Test
    fun `formatDate uses default pattern when exception occurs`() {
        val date = LocalDate.of(2024, 1, 15)
        // Invalid pattern should fallback to default dd.MM.yyyy
        val formatted = DateUtils.formatDate(date, "invalid-pattern")
        assertEquals("15.01.2024", formatted)
    }
}
