package com.istock.inventorymanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.ui.viewmodel.InventoryViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryInventoryScreen(
    categoryId: Long,
    navController: NavController,
    inventoryViewModel: InventoryViewModel = hiltViewModel()
) {
    val items by inventoryViewModel.getItemsByCategory(categoryId).collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopAppBar(
            title = { Text("Category Items") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Items in Category",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (items.isEmpty()) {
            Text(
                text = "No items found for this category.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { item ->
                    InventoryItemCard(item = item)
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: InventoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
        }
    }
}