package com.mobilecomputing.myfinance.screens.export_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilecomputing.myfinance.data.category.Category
import com.mobilecomputing.myfinance.data.entry.Entry
import com.mobilecomputing.myfinance.data.entry.EntryType
import com.mobilecomputing.myfinance.data.repository.CategoryRepository
import com.mobilecomputing.myfinance.data.repository.EntryRepository
import com.mobilecomputing.myfinance.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.io.OutputStream
import java.time.LocalDate
import java.time.ZoneId

class ExportViewModel(entryRepository: EntryRepository, categoryRepository: CategoryRepository) :
    ViewModel() {

    private val _selectedFormat = MutableStateFlow(ExportFormat.CSV)
    private val _selectedDateRange = MutableStateFlow(DateRange.THIS_MONTH)
    private val _isGenerating = MutableStateFlow(false)

    val uiState: StateFlow<ExportUiState> =
        combine(
            entryRepository.getAllEntries(),
            categoryRepository.getAllCategories(),
            _selectedFormat,
            _selectedDateRange,
            _isGenerating
        ) { entries, categories, format, range, isGenerating ->
            val filtered = filterEntries(entries, range)
            val estimatedSize = calculateEstimatedSize(filtered, format)

            this.categoriesMap = categories.associateBy { it.id }

            ExportUiState(
                selectedFormat = format,
                selectedDateRange = range,
                entriesCount = filtered.size,
                estimatedSizeKb = estimatedSize,
                isGenerating = isGenerating,
                filteredEntries = filtered
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ExportUiState()
            )

    private var categoriesMap: Map<String, Category> = emptyMap()

    fun updateFormat(format: ExportFormat) {
        _selectedFormat.value = format
    }

    fun updateDateRange(range: DateRange) {
        _selectedDateRange.value = range
    }

    private fun filterEntries(entries: List<Entry>, range: DateRange): List<Entry> {
        val now = LocalDate.now()
        return entries.filter { entry ->
            val entryDate = entry.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            when (range) {
                DateRange.THIS_MONTH -> entryDate.month == now.month && entryDate.year == now.year
                DateRange.LAST_MONTH -> {
                    val lastMonth = now.minusMonths(1)
                    entryDate.month == lastMonth.month && entryDate.year == lastMonth.year
                }

                DateRange.LAST_3_MONTHS -> {
                    val threeMonthsAgo = now.minusMonths(3)
                    entryDate.isAfter(threeMonthsAgo) || entryDate.isEqual(threeMonthsAgo)
                }

                DateRange.THIS_YEAR -> entryDate.year == now.year
                DateRange.ALL_TIME -> true
            }
        }
    }

    private fun calculateEstimatedSize(entries: List<Entry>, format: ExportFormat): Double {
        // Rough estimation
        return if (format == ExportFormat.CSV) {
            // Approx 100 bytes per line for CSV
            (entries.size * 100).toDouble() / 1024
        } else {
            // PDF is heavier
            (entries.size * 500).toDouble() / 1024
        }
    }

    fun generateCsv(outputStream: OutputStream) {
        val entries = uiState.value.filteredEntries
        val writer = outputStream.bufferedWriter()
        writer.use {
            // Header
            it.write("Date,Description,Category,Type,Amount\n")
            // Rows
            entries.forEach { entry ->
                val dateStr = DateUtils.formatDate(entry.date)
                val typeStr = if (entry.type == EntryType.INCOME) "Income" else "Expense"
                val categoryName = categoriesMap[entry.categoryId]?.title ?: "Unknown"

                val line =
                    "${escapeCsv(dateStr)},${escapeCsv(entry.description ?: "")},${
                        escapeCsv(
                            categoryName
                        )
                    },$typeStr,${entry.amount}\n"
                it.write(line)
            }
        }
    }

    private fun escapeCsv(value: String): String {
        var escaped = value.replace("\"", "\"\"")
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            escaped = "\"$escaped\""
        }
        return escaped
    }
}
