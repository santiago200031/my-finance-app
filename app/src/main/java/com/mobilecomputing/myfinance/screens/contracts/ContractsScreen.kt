package com.mobilecomputing.myfinance.screens.contracts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.data.models.contract.Contract
import com.mobilecomputing.myfinance.data.models.contract.ContractStatus
import com.mobilecomputing.myfinance.screens.contracts.data.getMockContracts
import com.mobilecomputing.myfinance.ui.theme.Orange
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
@Preview(showBackground = true)
fun ContractsScreen() {
    val contracts = getMockContracts()
    val activeCount = contracts.count { it.status == ContractStatus.ACTIVE }
    val expiringCount = contracts.count { it.status == ContractStatus.EXPIRING }

    val monthlyTotal = contracts.sumOf { it.amount }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                    count = activeCount.toString(),
                    label = "Active",
                    color = Color.Green,
                    modifier = Modifier.weight(1f)
            )
            SummaryCard(
                    count = expiringCount.toString(),
                    label = "Expiring",
                    color = Orange,
                    modifier = Modifier.weight(1f)
            )
            SummaryCard(
                    count = "$${String.format("%.2f", monthlyTotal)}",
                    label = "Monthly",
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(contracts) { contract -> ContractItem(contract) }
        }
    }
}

@Composable
fun SummaryCard(count: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                    text = count,
                    style = MaterialTheme.typography.titleLarge,
                    color = color,
                    fontWeight = FontWeight.Bold
            )
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}

@Composable
fun ContractItem(contract: Contract) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title and Status Badge
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = contract.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                StatusBadge(status = contract.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Frequency and Amount
            Text(
                    text =
                            "${contract.paymentCycle.name.lowercase().capitalize()} • $${contract.amount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Next Payment
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        imageVector = Icons.Default.CheckCircle, // Placeholder icon
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                        text = "Next payment: ${dateFormat.format(contract.nextPaymentDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Auto-renew or Expiration
            if (contract.autoRenewEnabled) {
                Surface(
                        color = Color(0xFFE1BEE7), // Light Purple
                        shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                            text = "Auto-renew enabled",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4A148C) // Dark Purple
                    )
                }
            } else {
                contract.endDate?.let { endDate ->
                    Text(
                            text = "Expires: ${dateFormat.format(endDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: ContractStatus) {
    val (backgroundColor, contentColor, text, icon) =
            when (status) {
                ContractStatus.ACTIVE ->
                        Quadruple(
                                Color(0xFFE8F5E9), // Light Green
                                Color(0xFF2E7D32), // Dark Green
                                "Active",
                                Icons.Default.CheckCircle
                        )
                ContractStatus.EXPIRING ->
                        Quadruple(
                                Color(0xFFFFF3E0), // Light Orange
                                Color(0xFFEF6C00), // Dark Orange
                                "Expiring",
                                Icons.Default.Warning
                        )
                else -> Quadruple(Color.LightGray, Color.Black, status.name, Icons.Default.Warning)
            }

    Surface(color = backgroundColor, shape = RoundedCornerShape(16.dp)) {
        Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
            )
        }
    }
}

// Helper class for destructuring
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
