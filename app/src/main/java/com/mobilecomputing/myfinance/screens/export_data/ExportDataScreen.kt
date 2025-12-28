package com.mobilecomputing.myfinance.screens.export_data

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.ui.AppViewModelProvider

@Composable
fun ExportDataScreen(
    viewModel: ExportViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("text/csv")
        ) { uri ->
            uri?.let {
                try {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        viewModel.generateCsv(outputStream)
                        Toast.makeText(context, "Export successful", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    ExportDataContent(
        uiState = uiState,
        onFormatSelected = viewModel::updateFormat,
        onDateRangeSelected = viewModel::updateDateRange,
        onGenerateClick = {
            launcher.launch("myfinance_export_${System.currentTimeMillis()}.csv")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ExportDataScreenPreview() {
    MaterialTheme {
        ExportDataContent(
            uiState =
                ExportUiState(
                    selectedFormat = ExportFormat.CSV,
                    selectedDateRange = DateRange.THIS_MONTH,
                    entriesCount = 42,
                    estimatedSizeKb = 15.6,
                    isGenerating = false
                ),
            onFormatSelected = {},
            onDateRangeSelected = {},
            onGenerateClick = {}
        )
    }
}
