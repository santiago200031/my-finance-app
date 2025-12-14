package com.mobilecomputing.myfinance.screens.contracts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobilecomputing.myfinance.screens.contracts.components.ContractItem
import com.mobilecomputing.myfinance.ui.AppViewModelProvider

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
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
        ) {
          items(uiState.contracts) { contract ->
            ContractItem(contract = contract, onContractClick = { /* Read only */})
          }
        }
      }
    }
  }
}
