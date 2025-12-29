package com.mobilecomputing.myfinance.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DEFAULT_PATTERN = "dd.MM.yyyy"

    fun formatDate(date: LocalDate, pattern: String = DEFAULT_PATTERN): String {
        return try {
            date.format(DateTimeFormatter.ofPattern(pattern))
        } catch (_: Exception) {
            date.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN))
        }
    }

    fun formatInputDate(date: LocalDate, pattern: String = DEFAULT_PATTERN): String {
        return formatDate(date, pattern)
    }

    fun parseInputDate(dateString: String, pattern: String = DEFAULT_PATTERN): LocalDate {
        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern))
        } catch (_: Exception) {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DEFAULT_PATTERN))
        }
    }

    fun formatDate(date: Date, pattern: String = DEFAULT_PATTERN): String {
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return formatDate(localDate, pattern)
    }

    fun isSameMonth(date1: Date, date2: Date): Boolean {
        val localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate1.year == localDate2.year && localDate1.month == localDate2.month
    }

    fun addMonths(date: Date, monthsToAdd: Long): Date {
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val newDate = localDate.plusMonths(monthsToAdd)
        return Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun formatMonthYear(date: Date): String {
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        // Display format example: "January 2024"
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
        return localDate.format(formatter)
    }

    fun getStartOfMonth(date: Date): Date {
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val startOfMonth = localDate.withDayOfMonth(1)
        return Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun getEndOfMonth(date: Date): Date {
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val endOfMonth = localDate.withDayOfMonth(localDate.lengthOfMonth())
        // End of the day
        return Date.from(endOfMonth.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant())
    }
}
