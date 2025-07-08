package com.istock.inventorymanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.istock.inventorymanager.data.model.Category
import com.istock.inventorymanager.ui.viewmodel.CategoryViewModel

// Helper function to get icon for category
private fun getCategoryIcon(categoryName: String): ImageVector {
    return when (categoryName.lowercase()) {
        "kitchen" -> Icons.Default.Restaurant
        "bedroom" -> Icons.Default.Hotel
        "bathroom" -> Icons.Default.Bathtub
        "living room", "livingroom" -> Icons.Default.Weekend
        "garage" -> Icons.Default.Garage
        "store room", "storeroom", "storage" -> Icons.Default.Inventory2
        "office" -> Icons.Default.Work
        "garden" -> Icons.Default.Park
        "laundry" -> Icons.Default.LocalLaundryService
        else -> Icons.Default.Folder
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(viewModel: CategoryViewModel = hiltViewModel()) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp)
        ) {
            // Header with search bar
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                            ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                            text = "Room Categories",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Search categories...") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            },
                            shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            } else {
                // Grid layout for categories
                LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                ) {
                    items(categories) { category ->
                        CategoryCard(
                                category = category,
                                onEdit = { editingCategory = category },
                                onDelete = { viewModel.deleteCategory(category) },
                                onClick = { /* Navigate to category items */}
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Category",
                    tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddCategoryDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, description ->
                    viewModel.addCategory(name, description)
                    showAddDialog = false
                }
        )
    }

    editingCategory?.let { category ->
        EditCategoryDialog(
                category = category,
                onDismiss = { editingCategory = null },
                onConfirm = { updatedCategory ->
                    viewModel.updateCategory(updatedCategory)
                    editingCategory = null
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
        category: Category,
        onEdit: () -> Unit,
        onDelete: () -> Unit,
        onClick: () -> Unit
) {
    val icon = getCategoryIcon(category.name)

    Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable { onClick() },
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
            ) {
                // Category Icon
                Icon(
                        imageVector = icon,
                        contentDescription = category.name,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Name
                Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (category.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            maxLines = 2
                    )
                }
            }

            // Action buttons
            Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Add Category") },
            text = {
                Column {
                    OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Category Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name.trim(), description.trim())
                            }
                        }
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditCategoryDialog(category: Category, onDismiss: () -> Unit, onConfirm: (Category) -> Unit) {
    var name by remember { mutableStateOf(category.name) }
    var description by remember { mutableStateOf(category.description) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Edit Category") },
            text = {
                Column {
                    OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Category Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                        category.copy(
                                                name = name.trim(),
                                                description = description.trim()
                                        )
                                )
                            }
                        }
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
