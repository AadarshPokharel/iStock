package com.istock.inventorymanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.ui.viewmodel.SimpleInventoryViewModel

@Composable
fun SimpleInventoryScreen(
    viewModel: SimpleInventoryViewModel = hiltViewModel(),
    onAddItem: () -> Unit = {},
    onEditItem: (InventoryItem) -> Unit = {}
) {
    val inventoryItems by viewModel.inventoryItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading inventory...")
                }
            }
        } else {
            Text(
                text = "Inventory Items (${inventoryItems.size})",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (inventoryItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items found. Add your first item!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn {
                    items(inventoryItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = { onEditItem(item) }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Quantity: ${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
