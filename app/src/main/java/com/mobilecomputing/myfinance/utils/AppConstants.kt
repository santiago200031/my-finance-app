package com.mobilecomputing.myfinance.utils

import androidx.compose.ui.unit.dp

object AppConstants {
    object DateFormatOption {
        const val GERMAN = "dd.MM.yyyy"
        const val US = "MM/dd/yyyy"
        const val ISO = "yyyy-MM-dd"
        const val TEXTUAL = "d MMM yyyy"

        val all = listOf(GERMAN, US, ISO, TEXTUAL)
    }

    val PADDING_XSMALL = 4.dp
    val PADDING_SMALL = 8.dp
    val PADDING_CHIP_HORIZONTAL = 12.dp
    val PADDING_MEDIUM = 16.dp
    val PADDING_LARGE = 24.dp

    val CORNER_RADIUS_SMALL = 8.dp
    val CORNER_RADIUS_MEDIUM = 16.dp
    val CORNER_RADIUS_BUTTON = 28.dp

    val ICON_SIZE_MEDIUM = 20.dp
    val ICON_SIZE_LARGE = 24.dp

    val BORDER_WIDTH = 1.dp
    val CARD_HEIGHT = 100.dp
    val BUTTON_HEIGHT = 56.dp
}
