package com.mobilecomputing.myfinance.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun ProfileCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(AppConstants.PADDING_MEDIUM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier =
                    Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                // Show initials if no image
                val initials = (user.firstName.take(1) + user.lastName.take(1)).uppercase()
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(AppConstants.PADDING_MEDIUM))

            Column {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
