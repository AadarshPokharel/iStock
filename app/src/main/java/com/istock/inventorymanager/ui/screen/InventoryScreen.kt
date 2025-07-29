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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.clickable // For Date TextFields
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.Calendar // For Date manipulation


@OptIn(ExperimentalMaterial3Api::class) // Added OptIn for DatePickerState
@Composable
fun EditInventoryItemDialog(
    item: InventoryItem,
    onDismiss: () -> Unit,
    onConfirm: (InventoryItem) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var description by remember { mutableStateOf(item.description) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var minStock by remember { mutableStateOf(item.minStockLevel.toString()) }
    var price by remember { mutableStateOf(String.format(Locale.US, "%.2f", item.price)) } // Assuming price is Double

    // --- Date States ---
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    var expirationDate by remember { mutableStateOf(item.expirationDate) }
    var showExpirationDatePicker by remember { mutableStateOf(false) }
    val expirationDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = item.expirationDate?.time,
        initialDisplayMode = DisplayMode.Picker // Or DisplayMode.Input
    )

    var warrantyDate by remember { mutableStateOf(item.warrantyDate) }
    var showWarrantyDatePicker by remember { mutableStateOf(false) }
    val warrantyDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = item.warrantyDate?.time,
        initialDisplayMode = DisplayMode.Picker
    )


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Inventory Item") },
        text = {
            Box(modifier = Modifier.heightIn(max = 500.dp)){
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
            )
                { // Use LazyColumn if the content might overflow
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = minStock,
                        onValueChange = { minStock = it },
                        label = { Text("Min Stock Level") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Expiration Date
                item {
                    OutlinedTextField(
                        value = expirationDate?.let { dateFormat.format(it) } ?: "Select Date",
                        onValueChange = { /* Read Only */ },
                        label = { Text("Expiration Date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showExpirationDatePicker = true },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Filled.DateRange,
                                contentDescription = "Select Expiration Date"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Warranty Date
                item {
                    OutlinedTextField(
                        value = warrantyDate?.let { dateFormat.format(it) } ?: "Select Date",
                        onValueChange = { /* Read Only */ },
                        label = { Text("Warranty Date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showWarrantyDatePicker = true },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Filled.DateRange,
                                contentDescription = "Select Warranty Date"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // for the save button
                item {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val updatedItem = item.copy(
                                    name = name.trim(),
                                    description = description.trim(),
                                    quantity = quantity.toIntOrNull() ?: item.quantity,
                                    minStockLevel = minStock.toIntOrNull() ?: item.minStockLevel,
                                    price = price.toDoubleOrNull() ?: item.price,
                                    expirationDate = expirationDate,
                                    warrantyDate = warrantyDate
                                )
                                onConfirm(updatedItem)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SAVE (within content)")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedItem = item.copy(
                        name = name.trim(),
                        description = description.trim(),
                        quantity = quantity.toIntOrNull() ?: item.quantity,
                        minStockLevel = minStock.toIntOrNull() ?: item.minStockLevel,
                        price = price.toDoubleOrNull() ?: item.price,
                        expirationDate = expirationDate,
                        warrantyDate = warrantyDate
                    )
                    onConfirm(updatedItem) // Pass the fully updated item
                }
            ) { Text("Save") } // Button text is "Save"
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )

    // --- Date Picker Dialogs ---
    if (showExpirationDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showExpirationDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    expirationDatePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        expirationDate = cal.time // Store as Date object
                    }
                    showExpirationDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showExpirationDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = expirationDatePickerState)
        }
    }

    if (showWarrantyDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showWarrantyDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    warrantyDatePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        warrantyDate = cal.time
                    }
                    showWarrantyDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showWarrantyDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = warrantyDatePickerState)
        }
    }
}
