package com.mobilecomputing.myfinance.screens.budget_planning.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobilecomputing.myfinance.data.service.CategoryBudgetStatus
import com.mobilecomputing.myfinance.ui.theme.BudgetGreen
import com.mobilecomputing.myfinance.ui.theme.BudgetOrange
import com.mobilecomputing.myfinance.ui.theme.BudgetRed
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun CategoryBudgetCard(status: CategoryBudgetStatus, onDeleteClick: () -> Unit) {
    val isOverBudget = status.spentAmount > status.category.budgetLimit
    val progressColor =
        when {
            status.percentUsed >= 100 -> BudgetRed
            status.percentUsed >= 80 -> BudgetOrange
            else -> BudgetGreen
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_SMALL)
    ) {
        Column(modifier = Modifier.padding(AppConstants.PADDING_MEDIUM)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(status.category.title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(AppConstants.PADDING_XSMALL))
                    if (isOverBudget) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Over Budget",
                            tint = BudgetRed,
                            modifier = Modifier.size(AppConstants.ICON_SIZE_SMALL)
                        )
                    } else if (status.percentUsed >= 80) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Near Limit",
                            tint = BudgetOrange,
                            modifier = Modifier.size(AppConstants.ICON_SIZE_SMALL)
                        )
                    } else {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "On Track",
                            tint = BudgetGreen,
                            modifier = Modifier.size(AppConstants.ICON_SIZE_SMALL)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${status.percentUsed}%",
                        color = progressColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        if (isOverBudget)
                            "$-${(status.spentAmount - status.category.budgetLimit).toInt()} left"
                        else "$${status.remainingAmount.toInt()} left",
                        color = if (isOverBudget) BudgetRed else BudgetGreen,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Category",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppConstants.PADDING_XSMALL))
            Text(
                "$${status.spentAmount.toInt()} / $${status.category.budgetLimit.toInt()}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))
            LinearProgressIndicator(
                progress = { (status.percentUsed / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )

            if (isOverBudget) {
                Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = BudgetRed,
                        modifier = Modifier.size(AppConstants.ICON_SIZE_SMALL)
                    )
                    Spacer(modifier = Modifier.width(AppConstants.PADDING_XSMALL))
                    Text(
                        "Over budget by $${(status.spentAmount - status.category.budgetLimit).toInt()}",
                        color = BudgetRed,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
