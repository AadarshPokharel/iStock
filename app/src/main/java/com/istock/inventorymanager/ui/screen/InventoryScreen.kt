package com.istock.inventorymanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.ui.viewmodel.CategoryViewModel
import com.istock.inventorymanager.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
        inventoryViewModel: InventoryViewModel = hiltViewModel(),
        categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val inventoryItems by inventoryViewModel.inventoryItems.collectAsState()
    val lowStockItems by inventoryViewModel.lowStockItems.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val isLoading by inventoryViewModel.isLoading.collectAsState()
    val selectedCategoryId by inventoryViewModel.selectedCategoryId.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<InventoryItem?>(null) }
    var showLowStockFilter by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "Inventory",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
            )

            Row {
                if (lowStockItems.isNotEmpty()) {
                    FilterChip(
                            onClick = {
                                showLowStockFilter = !showLowStockFilter
                                if (showLowStockFilter) {
                                    // Show only low stock items - this would require additional
                                    // logic
                                } else {
                                    inventoryViewModel.selectCategory(selectedCategoryId)
                                }
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Low Stock (${lowStockItems.size})")
                                }
                            },
                            selected = showLowStockFilter,
                            colors =
                                    FilterChipDefaults.filterChipColors(
                                            selectedContainerColor =
                                                    MaterialTheme.colorScheme.errorContainer
                                    )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                FloatingActionButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(56.dp)
                ) { Icon(Icons.Default.Add, contentDescription = "Add Item") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category filter
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                        onClick = { inventoryViewModel.selectCategory(null) },
                        label = { Text("All") },
                        selected = selectedCategoryId == null
                )
            }
            items(categories) { category ->
                FilterChip(
                        onClick = { inventoryViewModel.selectCategory(category.id) },
                        label = { Text(category.name) },
                        selected = selectedCategoryId == category.id
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        val itemsToShow = if (showLowStockFilter) lowStockItems else inventoryItems

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(itemsToShow) { item ->
                InventoryItemCard(
                        item = item,
                        onEdit = { editingItem = item },
                        onDelete = { inventoryViewModel.deleteItem(item) },
                )
            }
        }
    }

    if (showAddDialog) {
        AddInventoryItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, description, categoryId, quantity, minStock, expDate, warDate, _
                    -> // Renamed unused parameter 'price' to '_'
                    inventoryViewModel.addItem(
                            InventoryItem(
                                    name = name,
                                    description = description,
                                    categoryId = categoryId,
                                    quantity = quantity,
                                    minStockLevel = minStock,
                                    expirationDate = expDate,
                                    warrantyDate = warDate,
                                    price = 0.0,
                                    imagePath = "",
                                    barcode = "",
                                    location = "",
                                    notes = ""
                            )
                    )
                    showAddDialog = false
                }
        )
    }

    editingItem?.let { item ->
        EditInventoryItemDialog(
                item = item,
                onDismiss = { editingItem = null },
                onConfirm = { updatedItem ->
                    inventoryViewModel.updateItem(updatedItem)
                    editingItem = null
                }
        )
    }
}

@Composable
fun InventoryItemCard(
        item: InventoryItem,
        onEdit: () -> Unit,
        onDelete: () -> Unit,
) {
    val isLowStock = item.quantity <= item.minStockLevel
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (isLowStock) MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.surface
                    )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )

                    if (item.description.isNotEmpty()) {
                        Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                                text = "Qty: ${item.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color =
                                        if (isLowStock) MaterialTheme.colorScheme.error
                                        else Color.Unspecified
                        )

                        if (item.price > 0) {
                            Text(
                                    text = "$${String.format("%.2f", item.price)}",
                                    style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    item.expirationDate?.let { date ->
                        Text(
                                text = "Expires: ${dateFormat.format(date)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item.warrantyDate?.let { date ->
                        Text(
                                text = "Warranty: ${dateFormat.format(date)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }

            if (isLowStock) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "Low Stock Alert",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun AddInventoryItemDialog(
        onDismiss: () -> Unit,
        onConfirm: (String, String, Long, Int, Int, Date?, Date?, Double) -> Unit
) {
    // Simplified implementation - in a real app, this would have more fields
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add Inventory Item") },
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
                    // Add category dropdown here
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            if (name.isNotBlank() &&
                                            quantity.isNotBlank() &&
                                            selectedCategoryId != null
                            ) {
                                onConfirm(
                                        name.trim(),
                                        "",
                                        selectedCategoryId!!,
                                        quantity.toIntOrNull() ?: 0,
                                        0,
                                        null,
                                        null,
                                        0.0
                                )
                            }
                        }
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditInventoryItemDialog(
        item: InventoryItem,
        onDismiss: () -> Unit,
        onConfirm: (InventoryItem) -> Unit
) {
    // Simplified implementation
    var name by remember { mutableStateOf(item.name) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Edit Inventory Item") },
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
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            if (name.isNotBlank() && quantity.isNotBlank()) {
                                onConfirm(
                                        item.copy(
                                                name = name.trim(),
                                                quantity = quantity.toIntOrNull() ?: item.quantity
                                        )
                                )
                            }
                        }
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
