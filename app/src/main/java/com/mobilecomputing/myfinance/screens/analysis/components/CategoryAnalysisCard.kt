package com.mobilecomputing.myfinance.screens.analysis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.mobilecomputing.myfinance.screens.analysis.CategoryTotal
import com.mobilecomputing.myfinance.utils.AppConstants
import com.mobilecomputing.myfinance.utils.FormatUtils

@Composable
fun CategoryAnalysisCard(title: String, categories: List<CategoryTotal>, currency: String) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppConstants.PADDING_MEDIUM,
                    vertical = AppConstants.PADDING_SMALL
                ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(AppConstants.PADDING_MEDIUM)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (categories.isEmpty()) {
                Text(
                    text = "No data for this period",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                val totalAmount = categories.sumOf { it.amount }

                categories.forEachIndexed { index, category ->
                    CategoryRow(category = category, currency = currency, totalAmount = totalAmount)
                    if (index < categories.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryRow(category: CategoryTotal, currency: String, totalAmount: Double) {
    val percentage = if (totalAmount > 0) (category.amount / totalAmount).toFloat() else 0f
    val color = Color(category.colorHex.toColorInt())

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            // Color Indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = category.categoryName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = FormatUtils.formatCurrency(category.amount, currency),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Progress Bar
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(percentage)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(color)
            )
        }
    }
}
