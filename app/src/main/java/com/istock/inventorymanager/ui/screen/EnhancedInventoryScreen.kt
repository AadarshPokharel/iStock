package com.istock.inventorymanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.istock.inventorymanager.data.model.Category
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.ui.viewmodel.CategoryViewModel
import com.istock.inventorymanager.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
        inventoryViewModel: InventoryViewModel = hiltViewModel(),
        categoryViewModel: CategoryViewModel = hiltViewModel(),
        onAddItem: () -> Unit = {},
        onEditItem: (InventoryItem) -> Unit = {}
) {
    val inventoryItems by inventoryViewModel.inventoryItems.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val isLoading by inventoryViewModel.isLoading.collectAsState()
    val selectedCategoryId by inventoryViewModel.selectedCategoryId.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<InventoryItem?>(null) }
    var expandedFilters by remember { mutableStateOf(false) }
    var showLowStockOnly by remember { mutableStateOf(false) }
    var showExpiringOnly by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val today = Date()
    val weekFromNow =
            Calendar.getInstance()
                    .apply {
                        time = today
                        add(Calendar.DAY_OF_MONTH, 7)
                    }
                    .time

    // Filter items based on selected criteria
    val filteredItems =
            inventoryItems.filter { item ->
                val categoryMatch = selectedCategoryId?.let { item.categoryId == it } ?: true
                val lowStockMatch =
                        if (showLowStockOnly) item.quantity <= item.minStockLevel else true
                val expiringMatch =
                        if (showExpiringOnly) {
                            item.expirationDate?.let { it.before(weekFromNow) } ?: false
                        } else true

                categoryMatch && lowStockMatch && expiringMatch
            }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header with filters
        Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = "Inventory (${filteredItems.size})",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                    )

                    Row {
                        IconButton(onClick = { expandedFilters = !expandedFilters }) {
                            Icon(
                                    if (expandedFilters) Icons.Default.ExpandLess
                                    else Icons.Default.ExpandMore,
                                    contentDescription = "Filters"
                            )
                        }

                        FloatingActionButton(onClick = onAddItem, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item")
                        }
                    }
                }

                if (expandedFilters) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Category filter
                    LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                    ) {
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Additional filters
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                                onClick = { showLowStockOnly = !showLowStockOnly },
                                label = { Text("Low Stock") },
                                selected = showLowStockOnly,
                                leadingIcon =
                                        if (showLowStockOnly) {
                                            {
                                                Icon(
                                                        Icons.Default.Warning,
                                                        contentDescription = null
                                                )
                                            }
                                        } else null
                        )

                        FilterChip(
                                onClick = { showExpiringOnly = !showExpiringOnly },
                                label = { Text("Expiring Soon") },
                                selected = showExpiringOnly,
                                leadingIcon =
                                        if (showExpiringOnly) {
                                            {
                                                Icon(
                                                        Icons.Default.Schedule,
                                                        contentDescription = null
                                                )
                                            }
                                        } else null
                        )
                    }
                }
            }
        }

        if (filteredItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                            text = "No items found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                            text = "Add your first item or adjust filters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredItems) { item ->
                    InventoryItemCard(
                            item = item,
                            category = categories.find { it.id == item.categoryId },
                            onEdit = { onEditItem(item) },
                            onDelete = { showDeleteDialog = item },
                            onQuantityChange = { newQuantity ->
                                inventoryViewModel.updateQuantity(item.id, newQuantity)
                            },
                            dateFormatter = dateFormatter,
                            today = today
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { item ->
        AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Item") },
                text = {
                    Text(
                            "Are you sure you want to delete \"${item.name}\"? This action cannot be undone."
                    )
                },
                confirmButton = {
                    TextButton(
                            onClick = {
                                inventoryViewModel.deleteItem(item)
                                showDeleteDialog = null
                            }
                    ) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryItemCard(
        item: InventoryItem,
        category: Category?,
        onEdit: () -> Unit,
        onDelete: () -> Unit,
        onQuantityChange: (Int) -> Unit,
        dateFormatter: SimpleDateFormat,
        today: Date
) {
    var showQuantityDialog by remember { mutableStateOf(false) }

    // Check for low stock and expiry warnings
    val isLowStock = item.quantity <= item.minStockLevel
    val isExpiringSoon =
            item.expirationDate?.let { expiry ->
                val weekFromNow =
                        Calendar.getInstance()
                                .apply {
                                    time = today
                                    add(Calendar.DAY_OF_MONTH, 7)
                                }
                                .time
                expiry.before(weekFromNow)
            }
                    ?: false

    val isExpired = item.expirationDate?.before(today) ?: false

    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    when {
                                        isExpired -> MaterialTheme.colorScheme.errorContainer
                                        isLowStock ->
                                                MaterialTheme.colorScheme.tertiary.copy(
                                                        alpha = 0.3f
                                                )
                                        isExpiringSoon ->
                                                MaterialTheme.colorScheme.secondary.copy(
                                                        alpha = 0.3f
                                                )
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                    )
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Item image
            Box(
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
            ) {
                if (item.imagePath != null) {
                    AsyncImage(
                            model = item.imagePath,
                            contentDescription = "Item image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                    )
                } else {
                    Card(
                            modifier = Modifier.fillMaxSize(),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.secondaryContainer
                                    )
                    ) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                    Icons.Default.Image,
                                    contentDescription = "No image",
                                    modifier = Modifier.size(32.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Warning badges
                if (isExpired || isLowStock || isExpiringSoon) {
                    Badge(
                            modifier = Modifier.align(Alignment.TopEnd),
                            containerColor =
                                    when {
                                        isExpired -> MaterialTheme.colorScheme.error
                                        isLowStock -> MaterialTheme.colorScheme.tertiary
                                        else -> MaterialTheme.colorScheme.secondary
                                    }
                    ) {
                        Icon(
                                when {
                                    isExpired -> Icons.Default.Dangerous
                                    isLowStock -> Icons.Default.Warning
                                    else -> Icons.Default.Schedule
                                },
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                        )
                    }
                }
            }

            // Item details
            Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )

                Text(
                        text = category?.name ?: "Unknown Category",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                )

                if (item.description.isNotBlank()) {
                    Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity chip
                    SuggestionChip(
                            onClick = { showQuantityDialog = true },
                            label = { Text("Qty: ${item.quantity}") },
                            icon = { Icon(Icons.Default.Numbers, contentDescription = null) }
                    )

                    if (item.price > 0) {
                        Text(
                                text = "$${String.format("%.2f", item.price)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Dates info
                if (item.expirationDate != null || item.warrantyDate != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.expirationDate?.let { expiry ->
                            AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                                "Exp: ${dateFormatter.format(expiry)}",
                                                style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                                Icons.Default.Event,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors =
                                            AssistChipDefaults.assistChipColors(
                                                    containerColor =
                                                            if (isExpired)
                                                                    MaterialTheme.colorScheme.error
                                                            else if (isExpiringSoon)
                                                                    MaterialTheme.colorScheme
                                                                            .secondary
                                                            else
                                                                    MaterialTheme.colorScheme
                                                                            .surfaceVariant
                                            )
                            )
                        }

                        item.warrantyDate?.let { warranty ->
                            AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                                "War: ${dateFormatter.format(warranty)}",
                                                style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                                Icons.Default.Security,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                        )
                                    }
                            )
                        }
                    }
                }

                if (item.location.isNotBlank()) {
                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                                text = item.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Action buttons
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Quantity change dialog
    if (showQuantityDialog) {
        QuantityDialog(
                currentQuantity = item.quantity,
                onQuantityChanged = { newQuantity ->
                    onQuantityChange(newQuantity)
                    showQuantityDialog = false
                },
                onDismiss = { showQuantityDialog = false }
        )
    }
}

@Composable
fun QuantityDialog(currentQuantity: Int, onQuantityChanged: (Int) -> Unit, onDismiss: () -> Unit) {
    var quantity by remember { mutableStateOf(currentQuantity.toString()) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Update Quantity") },
            text = {
                OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            quantity.toIntOrNull()?.let { newQty ->
                                if (newQty >= 0) onQuantityChanged(newQty)
                            }
                        },
                        enabled = quantity.toIntOrNull()?.let { it >= 0 } ?: false
                ) { Text("Update") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
