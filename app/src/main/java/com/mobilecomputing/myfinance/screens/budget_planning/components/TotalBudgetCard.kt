package com.mobilecomputing.myfinance.screens.budget_planning.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobilecomputing.myfinance.data.service.BudgetOverview
import com.mobilecomputing.myfinance.ui.theme.PrimaryPurple
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun TotalBudgetCard(overview: BudgetOverview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryPurple),
        shape = RoundedCornerShape(AppConstants.CORNER_RADIUS_MEDIUM)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Monthly Budget", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$${overview.totalSpent.toInt()} / $${overview.totalBudget.toInt()}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${overview.percentUsed}% of budget used",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))
            LinearProgressIndicator(
                progress = { (overview.percentUsed / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}
