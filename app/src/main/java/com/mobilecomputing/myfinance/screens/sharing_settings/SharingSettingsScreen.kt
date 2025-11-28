package com.mobilecomputing.myfinance.screens.sharing_settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview(showBackground = true)
fun SharingSettingsScreen() {
    var shareWithPartner by remember { mutableStateOf(false) }
    var publicProfile by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Sharing Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        SharingItem(
            title = "Share with Partner",
            checked = shareWithPartner,
            onCheckedChange = { shareWithPartner = it }
        )
        SharingItem(
            title = "Public Profile",
            checked = publicProfile,
            onCheckedChange = { publicProfile = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Export */ }) {
            Text("Export Data")
        }
    }
}

@Composable
fun SharingItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
