package com.mobilecomputing.myfinance.screens.export_data

import com.mobilecomputing.myfinance.data.entry.Entry

data class ExportUiState(
    val selectedFormat: ExportFormat = ExportFormat.CSV,
    val selectedDateRange: DateRange = DateRange.THIS_MONTH,
    val entriesCount: Int = 0,
    val estimatedSizeKb: Double = 0.0,
    val isGenerating: Boolean = false,
    val filteredEntries: List<Entry> = emptyList()
)
