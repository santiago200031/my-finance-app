package com.mobilecomputing.myfinance.screens.contracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.contracts.components.ContractItem
import com.mobilecomputing.myfinance.ui.AppViewModelProvider
import com.mobilecomputing.myfinance.utils.AppConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedContractsScreen(
        userId: String,
        navigateBack: () -> Unit,
        viewModel: ContractsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
  val uiState by viewModel.uiState.collectAsState()

  LaunchedEffect(userId) { viewModel.switchUser(userId) }

  Scaffold(
          topBar = {
            TopAppBar(
                    title = { Text("Shared Contracts") },
                    navigationIcon = {
                      IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                      }
                    }
            )
          }
  ) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      if (uiState.contracts.isEmpty()) {
        Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
        ) { Text("No contracts shared by this user.") }
      } else {
        LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppConstants.PADDING_SMALL),
                contentPadding = PaddingValues(AppConstants.PADDING_MEDIUM)
        ) {
          items(uiState.contracts) { contract ->
            ContractItem(contract = contract, onContractClick = { /* Read only */})
          }
        }
      }
    }
  }
}
