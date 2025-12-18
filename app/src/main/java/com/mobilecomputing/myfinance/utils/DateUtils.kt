package com.mobilecomputing.myfinance.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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
}
