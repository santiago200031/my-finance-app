package com.mobilecomputing.myfinance.screens.contracts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.data.contract.Contract
import com.mobilecomputing.myfinance.data.contract.ContractStatus
import com.mobilecomputing.myfinance.data.contract.ContractType
import com.mobilecomputing.myfinance.data.contract.PaymentCycle
import com.mobilecomputing.myfinance.ui.theme.GreenIncome
import com.mobilecomputing.myfinance.ui.theme.Orange
import com.mobilecomputing.myfinance.utils.AppConstants
import com.mobilecomputing.myfinance.utils.DateUtils
import com.mobilecomputing.myfinance.utils.FormatUtils
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

@Composable
fun ContractItem(contract: Contract, onContractClick: () -> Unit = {}) {
    val isExpiring =
        contract.status == ContractStatus.ACTIVE &&
                contract.endDate != null &&
                ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    contract.endDate
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                ) in 0..30

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppConstants.PADDING_MEDIUM)
                .clickable { onContractClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier
            .padding(AppConstants.PADDING_MEDIUM)
            .fillMaxWidth()) {
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

                val (badgeText, badgeColor) =
                    if (isExpiring) {
                        "Expiring" to Orange
                    } else if (contract.status == ContractStatus.ACTIVE) {
                        "Active" to GreenIncome
                    } else {
                        "Cancelled" to Color.Gray
                    }

                Box(
                    modifier =
                        Modifier
                            .background(
                                color = badgeColor.copy(alpha = 0.1f),
                                shape =
                                    RoundedCornerShape(
                                        AppConstants.CORNER_RADIUS_MEDIUM
                                    )
                            )
                            .padding(
                                horizontal = AppConstants.PADDING_SMALL,
                                vertical = AppConstants.PADDING_XSMALL
                            )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppConstants.PADDING_XSMALL))

            // Monthly . Cost
            Text(
                text =
                    "${contract.paymentCycle.name} • ${FormatUtils.formatUSAmount(contract.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))

            // Next Payment
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .padding(end = AppConstants.PADDING_XSMALL)
                            .width(AppConstants.PADDING_MEDIUM),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text =
                        "Next payment: ${
                            DateUtils.formatDate(
                                contract.nextPaymentDate.toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            )
                        }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Auto-renew badge
            if (contract.autoRenewEnabled && contract.status == ContractStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))
                Box(
                    modifier =
                        Modifier
                            .background(
                                color =
                                    MaterialTheme.colorScheme.primaryContainer
                                        .copy(alpha = 0.5f),
                                shape =
                                    RoundedCornerShape(
                                        AppConstants.CORNER_RADIUS_SMALL
                                    )
                            )
                            .padding(
                                horizontal = AppConstants.PADDING_SMALL,
                                vertical = AppConstants.PADDING_XSMALL
                            )
                ) {
                    Text(
                        "Auto-renew enabled",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContractItemPreview() {
    val dummyContract =
        Contract(
            id = "1",
            title = "Netflix Subscription",
            amount = 12.99,
            paymentCycle = PaymentCycle.MONTHLY,
            type = ContractType.EXPENSE,
            startDate = Date(),
            nextPaymentDate = Date(),
            autoRenewEnabled = true,
            status = ContractStatus.ACTIVE,
            totalAmount = null
        )
    ContractItem(contract = dummyContract)
}
