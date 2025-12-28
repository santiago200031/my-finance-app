package com.mobilecomputing.myfinance.screens.export_data

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun FormatCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val backgroundColor = if (isSelected) PrimaryPurple else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val border =
        if (isSelected) null
        else
            BorderStroke(
                AppConstants.BORDER_WIDTH,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

    Card(
        modifier =
            modifier
                .height(AppConstants.CARD_HEIGHT)
                .clickable(enabled = enabled, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border,
        shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_MEDIUM)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppConstants.PADDING_CHIP_HORIZONTAL),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor.copy(alpha = if (enabled) 1f else 0.5f),
                modifier = Modifier.size(AppConstants.ICON_SIZE_LARGE)
            )
            Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor.copy(alpha = if (enabled) 1f else 0.5f),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = if (enabled) 0.8f else 0.4f)
            )
        }
    }
}