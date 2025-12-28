package com.mobilecomputing.myfinance.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val DISPLAY_PATTERN = AppConstants.DATE_FORMAT_DISPLAY
    private val displayFormatter = DateTimeFormatter.ofPattern(DISPLAY_PATTERN)

    private const val INPUT_PATTERN = AppConstants.DATE_FORMAT_INPUT
    private val inputFormatter = DateTimeFormatter.ofPattern(INPUT_PATTERN)

    fun formatDate(date: LocalDate): String {
        return date.format(displayFormatter)
    }

    fun formatInputDate(date: LocalDate): String {
        return date.format(inputFormatter)
    }

    fun parseInputDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, inputFormatter)
    }

    fun formatDate(date: Date): String {
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return formatDate(localDate)
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
