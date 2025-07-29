package com.istock.inventorymanager.ui.screen

import android.Manifest
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.istock.inventorymanager.data.model.Category
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.ui.util.CameraUtil
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    item: InventoryItem? = null,
    categories: List<Category>,
    onSave: (InventoryItem) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var selectedCategory by remember {
        mutableStateOf(categories.find { it.id == item?.categoryId } ?: categories.firstOrNull())
    }
    var quantity by remember { mutableStateOf(item?.quantity?.toString() ?: "") }
    var minStockLevel by remember { mutableStateOf(item?.minStockLevel?.toString() ?: "") }
    var price by remember { mutableStateOf(item?.price?.toString() ?: "") }
    var location by remember { mutableStateOf(item?.location ?: "") }
    var notes by remember { mutableStateOf(item?.notes ?: "") }
    var barcode by remember { mutableStateOf(item?.barcode ?: "") }
    var imagePath by remember { mutableStateOf(item?.imagePath) }

    var expirationDate by remember { mutableStateOf(item?.expirationDate) }
    var warrantyDate by remember { mutableStateOf(item?.warrantyDate) }

    var showCamera by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerType by remember { mutableStateOf("expiration") }
    var expandedCategory by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val imageCapture = remember { ImageCapture.Builder().build() }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (item == null) "Add New Item" else "Edit Item",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }
            }
        },
//        bottomBar = {
//            Surface(modifier = Modifier
//                .fillMaxWidth()
//                .navigationBarsPadding(),
//            shadowElevation = 8.dp
//            ) {
//                Button(
//                    onClick = {
//                        if (name.isNotBlank() && selectedCategory != null && quantity.isNotBlank()) {
//                            val newItem = InventoryItem(
//                                id = item?.id ?: 0,
//                                name = name.trim(),
//                                description = description.trim(),
//                                categoryId = selectedCategory!!.id,
//                                quantity = quantity.toIntOrNull() ?: 0,
//                                minStockLevel = minStockLevel.toIntOrNull() ?: 0,
//                                expirationDate = expirationDate,
//                                warrantyDate = warrantyDate,
//                                price = price.toDoubleOrNull() ?: 0.0,
//                                imagePath = imagePath,
//                                barcode = barcode.trim().ifEmpty { null }, // commented barcode in the code below
//                                location = location.trim(), // commented location in the code below
//                                notes = notes.trim(),
//                                createdAt = item?.createdAt ?: Date(),
//                                updatedAt = Date()
//                            )
//                            onSave(newItem)
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth().padding(16.dp),
//                    enabled = name.isNotBlank() && selectedCategory != null && quantity.isNotBlank()
//                ) {
//                    Icon(Icons.Default.Save, contentDescription = null)
//                    Spacer(modifier = Modifier.width(120.dp))
//                    Text(if (item == null) "Add Item" else "Update Item")
//                }
//            }
//        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            // Spacer(modifier = Modifier.height(16.dp))

            // Image Section
            Card(
                modifier =
                    Modifier.fillMaxWidth().height(200.dp).clickable {
                        if (cameraPermissionState.status.isGranted) {
                            showCamera = true
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (imagePath != null) {
                        AsyncImage(
                            model = imagePath,
                            contentDescription = "Item image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Delete image button
                        IconButton(
                            onClick = { imagePath = null },
                            modifier =
                                Modifier.align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.5f),
                                        CircleShape
                                    )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete image",
                                tint = Color.White
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Add image",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Tap to add image",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Basic Information
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Item Name *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Inventory, contentDescription = null) }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) }
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            // Quantity and Stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) }
                )

                OutlinedTextField(
                    value = minStockLevel,
                    onValueChange = { minStockLevel = it },
                    label = { Text("Min Stock") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) }
                )
            }

            // Price and Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
                )

//                OutlinedTextField(
//                    value = location,
//                    onValueChange = { location = it },
//                    label = { Text("Location") },
//                    modifier = Modifier.weight(1f),
//                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
//                )
            }

            // Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = expirationDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = {},
                    label = { Text("Expiry Date") },
                    modifier =
                        Modifier.weight(1f).clickable {
                            datePickerType = "expiration"
                            showDatePicker = true
                        },
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
                    trailingIcon = {
                        if (expirationDate != null) {
                            IconButton(onClick = { expirationDate = null }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear date")
                            }
                        }
                    }
                )

                OutlinedTextField(
                    value = warrantyDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = {},
                    label = { Text("Warranty Date") },
                    modifier =
                        Modifier.weight(1f).clickable {
                            datePickerType = "warranty"
                            showDatePicker = true
                        },
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
                    trailingIcon = {
                        if (warrantyDate != null) {
                            IconButton(onClick = { warrantyDate = null }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear date")
                            }
                        }
                    }
                )
            }

            // Barcode
//            OutlinedTextField(
//                value = barcode,
//                onValueChange = { barcode = it },
//                label = { Text("Barcode/SKU") },
//                modifier = Modifier.fillMaxWidth(),
//                leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) }
//            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                leadingIcon = {
                    Icon(Icons.Filled.Notes, contentDescription = null)
                }
            )
            // Save Button directly under Notes
            Button(
                onClick = {
                    if (name.isNotBlank() && selectedCategory != null && quantity.isNotBlank()) {
                        val newItem = InventoryItem(
                            id = item?.id ?: 0,
                            name = name.trim(),
                            description = description.trim(),
                            categoryId = selectedCategory!!.id,
                            quantity = quantity.toIntOrNull() ?: 0,
                            minStockLevel = minStockLevel.toIntOrNull() ?: 0,
                            expirationDate = expirationDate,
                            warrantyDate = warrantyDate,
                            price = price.toDoubleOrNull() ?: 0.0,
                            imagePath = imagePath,
                            barcode = barcode.trim().ifEmpty { null },
                            location = location.trim(),
                            notes = notes.trim(),
                            createdAt = item?.createdAt ?: Date(),
                            updatedAt = Date()
                        )
                        onSave(newItem)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = name.isNotBlank() && selectedCategory != null && quantity.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (item == null) "Add Item" else "Update Item")
            }


            // Add bottom padding for better scrolling experience
            Spacer(modifier = Modifier.height(80.dp)) // Extra space for the bottom button
        }
        // Ensure the bottom bar is always visible
        // Dialogs should be outside the scrollable content
        if (showCamera && cameraPermissionState.status.isGranted) {
            CameraDialog(
                imageCapture = imageCapture,
                onImageCaptured = { uri ->
                    imagePath = uri.toString()
                    showCamera = false
                },
                onDismiss = { showCamera = false }
            )
        }
        if (showDatePicker) {
            DatePickerDialog(
                onDateSelected = { selectedDate ->
                    when (datePickerType) {
                        "expiration" -> expirationDate = selectedDate
                        "warranty" -> warrantyDate = selectedDate
                    }
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }

    // Camera Dialog
    if (showCamera && cameraPermissionState.status.isGranted) {
        CameraDialog(
            imageCapture = imageCapture,
            onImageCaptured = { uri ->
                imagePath = uri.toString()
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDate ->
                when (datePickerType) {
                    "expiration" -> expirationDate = selectedDate
                    "warranty" -> warrantyDate = selectedDate
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun CameraDialog(
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().height(400.dp), shape = RoundedCornerShape(16.dp)) {
            Column {
                Box(modifier = Modifier.weight(1f)) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val executor = ContextCompat.getMainExecutor(ctx)
                            cameraProviderFuture.addListener(
                                {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview =
                                        Preview.Builder().build().also {
                                            it.setSurfaceProvider(
                                                previewView.surfaceProvider
                                            )
                                        }

                                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageCapture
                                        )
                                    } catch (exc: Exception) {
                                        // Handle exception
                                    }
                                },
                                executor
                            )
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Capture button
                    FloatingActionButton(
                        onClick = {
                            val outputFile = CameraUtil.createImageFile(context)
                            CameraUtil.captureImage(
                                imageCapture = imageCapture,
                                outputFile = outputFile,
                                onSuccess = onImageCaptured,
                                onError = { /* Handle error */}
                            )
                        },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                    ) { Icon(Icons.Default.CameraAlt, contentDescription = "Capture") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(onDateSelected: (Date) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) { DatePicker(state = datePickerState) }
}
