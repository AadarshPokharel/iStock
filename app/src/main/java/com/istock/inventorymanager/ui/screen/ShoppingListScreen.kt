package com.istock.inventorymanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.istock.inventorymanager.data.model.ShoppingListItem
import com.istock.inventorymanager.ui.viewmodel.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel = hiltViewModel()) {
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val pendingItems by viewModel.pendingItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showCompleted by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "Shopping List",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
            )

            Row {
                FilterChip(
                        onClick = { showCompleted = !showCompleted },
                        label = { Text(if (showCompleted) "Show Pending" else "Show All") },
                        selected = showCompleted
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(56.dp)
                ) { Icon(Icons.Default.Add, contentDescription = "Add Item") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            text = "${pendingItems.size}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )
                    Text(text = "Pending", style = MaterialTheme.typography.bodyMedium)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            text = "${shoppingItems.size - pendingItems.size}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )
                    Text(text = "Completed", style = MaterialTheme.typography.bodyMedium)
                }

                if (shoppingItems.any { it.isCompleted }) {
                    TextButton(onClick = { viewModel.deleteCompletedItems() }) {
                        Text("Clear Completed")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        val itemsToShow = if (showCompleted) shoppingItems else pendingItems

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(itemsToShow) { item ->
                ShoppingListItemCard(
                        item = item,
                        onToggleComplete = {
                            viewModel.toggleCompletion(item.id, !item.isCompleted)
                        },
                        onDelete = { viewModel.deleteShoppingItem(item) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddShoppingItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, quantity, priority, notes ->
                    viewModel.addShoppingItem(name, quantity, priority, notes)
                    showAddDialog = false
                }
        )
    }
}

@Composable
fun ShoppingListItemCard(
        item: ShoppingListItem,
        onToggleComplete: () -> Unit,
        onDelete: () -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (item.isCompleted) MaterialTheme.colorScheme.surfaceVariant
                                    else MaterialTheme.colorScheme.surface
                    )
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = item.isCompleted, onCheckedChange = { onToggleComplete() })

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        textDecoration =
                                if (item.isCompleted) TextDecoration.LineThrough
                                else TextDecoration.None,
                        color =
                                if (item.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                                else Color.Unspecified
                )

                Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = "Qty: ${item.quantity}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Priority indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (item.priority) {
                            3 -> {
                                Icon(
                                        Icons.Default.Error,
                                        contentDescription = "High Priority",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                )
                                Text(
                                        text = "High",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                )
                            }
                            2 -> {
                                Text(
                                        text = "Medium",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            else -> {
                                Text(
                                        text = "Low",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (item.notes.isNotEmpty()) {
                    Text(
                            text = item.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun AddShoppingItemDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var priority by remember { mutableStateOf(1) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Item Name") },
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text("Quantity") },
                            modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Priority:", style = MaterialTheme.typography.bodyMedium)
                    Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                                onClick = { priority = 1 },
                                label = { Text("Low") },
                                selected = priority == 1
                        )
                        FilterChip(
                                onClick = { priority = 2 },
                                label = { Text("Medium") },
                                selected = priority == 2
                        )
                        FilterChip(
                                onClick = { priority = 3 },
                                label = { Text("High") },
                                selected = priority == 3
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                        name.trim(),
                                        quantity.toIntOrNull() ?: 1,
                                        priority,
                                        notes.trim()
                                )
                            }
                        }
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
