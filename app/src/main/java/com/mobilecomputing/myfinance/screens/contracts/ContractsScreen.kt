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
import com.mobilecomputing.myfinance.data.models.transaction.TransactionType
import com.mobilecomputing.myfinance.data.services.FinanceService
import com.mobilecomputing.myfinance.screens.contracts.data.getMockContracts
import com.mobilecomputing.myfinance.ui.theme.DarkGreenContent
import com.mobilecomputing.myfinance.ui.theme.DarkOrangeContent
import com.mobilecomputing.myfinance.ui.theme.DisabledContainer
import com.mobilecomputing.myfinance.ui.theme.DisabledContent
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.LightGreenContainer
import com.mobilecomputing.myfinance.ui.theme.LightOrangeContainer
import com.mobilecomputing.myfinance.ui.theme.Orange
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.ui.theme.RedExpense
import com.mobilecomputing.myfinance.ui.theme.SecondaryPurple
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
@Preview(showBackground = true)
fun ContractsScreen() {
    val contracts = getMockContracts()
    val financeService = FinanceService()

    val activeCount = financeService.getActiveContractsCount(contracts)
    val expiringCount = financeService.getExpiringContractsCount(contracts)
    val monthlyTotal = financeService.calculateNetMonthly(contracts)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                count = activeCount.toString(),
                label = "Active",
                countColor = GreenIncome,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                count = expiringCount.toString(),
                label = "Expiring",
                countColor = Orange,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                count = "$${String.format("%.2f", monthlyTotal)}",
                label = "Net Monthly",
                countColor = Color.White,
                containerColor = PrimaryPurple,
                contentColor = Color.White,
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
fun SummaryCard(
    count: String,
    label: String,
    countColor: Color,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                color = countColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ContractItem(contract: Contract) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contract.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text =
                        "${
                            contract.paymentCycle.name.lowercase().capitalize()
                        } • Next: ${dateFormat.format(contract.nextPaymentDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (contract.autoRenewEnabled) {
                    Text(
                        text = "Auto-renew enabled",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryPurple
                    )
                } else {
                    contract.endDate?.let { endDate ->
                        Text(
                            text = "Expires: ${dateFormat.format(endDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Orange
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                val amountText =
                    if (contract.type == TransactionType.INCOME) "+$${contract.amount}"
                    else "-$${contract.amount}"
                val amountColor =
                    if (contract.type == TransactionType.INCOME) GreenIncome else RedExpense

                Text(
                    text = amountText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = contract.status)
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
                    LightGreenContainer,
                    DarkGreenContent,
                    "Active",
                    Icons.Default.CheckCircle
                )

            ContractStatus.EXPIRING ->
                Quadruple(
                    LightOrangeContainer,
                    DarkOrangeContent,
                    "Expiring",
                    Icons.Default.Warning
                )

            else ->
                Quadruple(
                    DisabledContainer,
                    DisabledContent,
                    status.name,
                    Icons.Default.Warning
                )
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
