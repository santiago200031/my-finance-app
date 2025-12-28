package com.mobilecomputing.myfinance.screens.export_data.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun DateRangeOption(label: String, onSelect: () -> Unit, selected: Boolean) {
    val backgroundColor = if (selected) PrimaryPurple else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppConstants.CORNER_RADIUS_SMALL))
                .background(backgroundColor)
                .clickable { onSelect() }
                .padding(
                    vertical = AppConstants.PADDING_CHIP_HORIZONTAL,
                    horizontal = AppConstants.PADDING_MEDIUM
                )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(AppConstants.ICON_SIZE_MEDIUM)
            )
            Spacer(modifier = Modifier.width(AppConstants.PADDING_MEDIUM))
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = contentColor)
            Spacer(modifier = Modifier.weight(1f))
            if (selected) {
                Box(
                    modifier =
                        Modifier
                            .size(AppConstants.PADDING_SMALL)
                            .clip(RoundedCornerShape(AppConstants.PADDING_XSMALL))
                            .background(Color.White)
                )
            }
        }
    }
}
