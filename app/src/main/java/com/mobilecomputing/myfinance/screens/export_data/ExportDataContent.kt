package com.mobilecomputing.myfinance.screens.export_data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.utils.AppConstants
import java.util.Locale

@Composable
fun ExportDataContent(
    uiState: ExportUiState,
    onFormatSelected: (ExportFormat) -> Unit,
    onDateRangeSelected: (DateRange) -> Unit,
    onGenerateClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppConstants.PADDING_MEDIUM),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Export Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = AppConstants.PADDING_MEDIUM)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_LARGE)
        ) {
            // Select Format Section
            Text(
                "Select Format",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(AppConstants.PADDING_MEDIUM)) {
                // TODO(For later implementation)
                //                FormatCard(
                //                    title = "PDF Report",
                //                    subtitle = "Formatted document",
                //                    icon = Icons.Default.Description,
                //                    isSelected = uiState.selectedFormat == ExportFormat.PDF,
                //                    onClick = { /* PDF not implemented yet */ },
                //                    modifier = Modifier.weight(1f),
                //                    enabled = false // Disable PDF for now
                //                )
                FormatCard(
                    title = "CSV File",
                    subtitle = "Spreadsheet data",
                    icon = Icons.AutoMirrored.Filled.InsertDriveFile,
                    isSelected = uiState.selectedFormat == ExportFormat.CSV,
                    onClick = { onFormatSelected(ExportFormat.CSV) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Date Range Section
            Text(
                "Date Range",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)) {
                DateRangeOption(
                    label = "This Month",
                    onSelect = { onDateRangeSelected(DateRange.THIS_MONTH) },
                    selected = uiState.selectedDateRange == DateRange.THIS_MONTH
                )
                DateRangeOption(
                    label = "Last Month",
                    onSelect = { onDateRangeSelected(DateRange.LAST_MONTH) },
                    selected = uiState.selectedDateRange == DateRange.LAST_MONTH
                )
                DateRangeOption(
                    label = "Last 3 Months",
                    onSelect = { onDateRangeSelected(DateRange.LAST_3_MONTHS) },
                    selected = uiState.selectedDateRange == DateRange.LAST_3_MONTHS
                )
                DateRangeOption(
                    label = "This Year",
                    onSelect = { onDateRangeSelected(DateRange.THIS_YEAR) },
                    selected = uiState.selectedDateRange == DateRange.THIS_YEAR
                )
                DateRangeOption(
                    label = "All Time",
                    onSelect = { onDateRangeSelected(DateRange.ALL_TIME) },
                    selected = uiState.selectedDateRange == DateRange.ALL_TIME
                )
            }

            // Export Preview Section
            Text(
                "Export Preview",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.5f
                            )
                    ),
                shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_MEDIUM)
            ) {
                Column(
                    modifier = Modifier.padding(AppConstants.PADDING_MEDIUM),
                    verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL)
                ) {
                    PreviewRow("Format:", uiState.selectedFormat.name)
                    PreviewRow("Date Range:", getDateRangeLabel(uiState.selectedDateRange))
                    PreviewRow(
                        "Estimated Size:",
                        "~${
                            String.format(
                                Locale.getDefault(),
                                "%.2f",
                                uiState.estimatedSizeKb
                            )
                        } KB"
                    )
                    PreviewRow("Entries Included:", "${uiState.entriesCount} transactions")
                }
            }

            Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))

            Button(
                onClick = onGenerateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppConstants.BUTTON_HEIGHT),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_BUTTON)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(AppConstants.ICON_SIZE_MEDIUM)
                )
                Spacer(modifier = Modifier.width(AppConstants.PADDING_SMALL))
                Text("Generate and Download", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

fun getDateRangeLabel(range: DateRange): String {
    return when (range) {
        DateRange.THIS_MONTH -> "This Month"
        DateRange.LAST_MONTH -> "Last Month"
        DateRange.LAST_3_MONTHS -> "Last 3 Months"
        DateRange.THIS_YEAR -> "This Year"
        DateRange.ALL_TIME -> "All Time"
    }
}
