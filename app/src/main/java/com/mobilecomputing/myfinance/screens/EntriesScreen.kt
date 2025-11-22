package com.mobilecomputing.myfinance.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.data.models.transaction.TransactionType
import com.mobilecomputing.myfinance.screens.dashboard.data.getMockTransactions
import com.mobilecomputing.myfinance.screens.entries.components.FilterButtons
import com.mobilecomputing.myfinance.screens.entries.components.TransactionFilter
import com.mobilecomputing.myfinance.ui.components.SearchBar
import com.mobilecomputing.myfinance.ui.components.TransactionItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun EntriesScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(TransactionFilter.ALL) }

    val allTransactions = getMockTransactions()

    val filteredTransactions = allTransactions.filter { transaction ->
        when (selectedFilter) {
            TransactionFilter.ALL -> true
            TransactionFilter.INCOME -> transaction.type == TransactionType.INCOME
            TransactionFilter.EXPENSE -> transaction.type == TransactionType.EXPENSE
        }
    }.filter { transaction ->
        searchQuery.isEmpty() ||
                transaction.description.contains(searchQuery, ignoreCase = true) ||
                transaction.categoryName.contains(searchQuery, ignoreCase = true)
    }.sortedByDescending { it.date }

    val groupedTransactions = filteredTransactions.groupBy { transaction ->
        formatDateHeader(transaction.date)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            onSearch = { query -> searchQuery = query }
        )

        FilterButtons(
            selectedFilter = selectedFilter,
            onFilterSelected = { filter -> selectedFilter = filter }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            groupedTransactions.forEach { (dateHeader, transactions) ->
                item {
                    Text(
                        text = dateHeader,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        showPadding = false,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

private fun formatDateHeader(date: Date): String {
    val calendar = Calendar.getInstance()
    val today = calendar.time

    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = calendar.time

    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(date)
    val todayString = dateFormat.format(today)
    val yesterdayString = dateFormat.format(yesterday)

    return when (dateString) {
        todayString -> "Today"
        yesterdayString -> "Yesterday"
        else -> dateString
    }
}