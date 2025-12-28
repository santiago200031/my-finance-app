package com.mobilecomputing.myfinance.screens.sharing_settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mobilecomputing.myfinance.data.models.User
import com.mobilecomputing.myfinance.utils.AppConstants

@Composable
fun SharingSettingsContent(
    emailInput: String,
    onEmailInputChange: (String) -> Unit,
    onAddTrustedEmail: () -> Unit,
    currentUser: User?,
    onViewContracts: (String) -> Unit
) {
    val targetInfo =
        when (currentUser?.id) {
            "s-svilla" ->
                Pair("villavicencioandrs@gmail.com", "View villavicencioandrs's Contracts")

            "villavicencioandrs" ->
                Pair("s-svilla@haw-landshut.de", "View s-svilla's Contracts")

            else -> null
        }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(AppConstants.PADDING_MEDIUM)) {
        Text("Sharing Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(AppConstants.PADDING_MEDIUM))

        // Trusted Email Section
        OutlinedTextField(
            value = emailInput,
            onValueChange = onEmailInputChange,
            label = { Text("Trusted Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(AppConstants.PADDING_SMALL))
        Button(onClick = onAddTrustedEmail, modifier = Modifier.align(Alignment.End)) {
            Text("Add Trusted User")
        }

        Spacer(modifier = Modifier.height(AppConstants.PADDING_LARGE))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(AppConstants.PADDING_LARGE))

        if (targetInfo != null) {
            Button(
                onClick = { onViewContracts(targetInfo.first) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(targetInfo.second) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharingSettingsScreenPreview() {
    MaterialTheme {
        SharingSettingsContent(
            emailInput = "s-svilla@haw-landshut.de",
            onEmailInputChange = {},
            onAddTrustedEmail = {},
            currentUser =
                User(
                    id = "s-svilla",
                    email = "s-svilla@haw-landshut.de",
                    firstName = "Santiago",
                    lastName = "Villavicencio"
                ),
            onViewContracts = {}
        )
    }
}
