package com.istock.inventorymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.data.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class InventoryViewModel @Inject constructor(private val inventoryRepository: InventoryRepository) :
        ViewModel() {

    private val _allItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    
    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.asStateFlow()

    private val _lowStockItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val lowStockItems: StateFlow<List<InventoryItem>> = _lowStockItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    init {
        loadAllItems()
        loadLowStockItems()
        
        // Update filtered items when allItems or selectedCategoryId changes
        viewModelScope.launch {
            combine(_allItems, _selectedCategoryId) { items, categoryId ->
                if (categoryId == null) items
                else items.filter { it.categoryId == categoryId }
            }.collect { filteredItems ->
                _inventoryItems.value = filteredItems
            }
        }
    }

    private fun loadAllItems() {
        viewModelScope.launch {
            _isLoading.value = true
            var isFirstEmission = true
            inventoryRepository.getAllItems().collect { items -> 
                _allItems.value = items
                if (isFirstEmission) {
                    _isLoading.value = false
                    isFirstEmission = false
                }
            }
        }
    }

    private fun loadLowStockItems() {
        viewModelScope.launch {
            inventoryRepository.getLowStockItems().collect { items -> 
                _lowStockItems.value = items 
            }
        }
    }

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun addItem(item: InventoryItem) {
        viewModelScope.launch {
            inventoryRepository.insertItem(item)
        }
    }

    fun updateItem(item: InventoryItem) {
        viewModelScope.launch {
            inventoryRepository.updateItem(item.copy(updatedAt = Date()))
        }
    }

    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch {
            inventoryRepository.deleteItem(item)
        }
    }

    fun updateQuantity(itemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            inventoryRepository.updateQuantity(itemId, newQuantity)
        }
    }

    fun getExpiringItems(): StateFlow<List<InventoryItem>> {
        val expiringItems = MutableStateFlow<List<InventoryItem>>(emptyList())
        viewModelScope.launch {
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 7)
            val weekFromNow = calendar.time
            
            _allItems.collect { items ->
                expiringItems.value = items.filter { item ->
                    item.expirationDate?.let { it.before(weekFromNow) && it.after(Date()) } ?: false
                }
            }
        }
        return expiringItems.asStateFlow()
    }

    fun getWarrantyExpiringItems(): StateFlow<List<InventoryItem>> {
        val warrantyExpiringItems = MutableStateFlow<List<InventoryItem>>(emptyList())
        viewModelScope.launch {
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 30)
            val monthFromNow = calendar.time
            
            _allItems.collect { items ->
                warrantyExpiringItems.value = items.filter { item ->
                    item.warrantyDate?.let { it.before(monthFromNow) && it.after(Date()) } ?: false
                }
            }
        }
        return warrantyExpiringItems.asStateFlow()
    }

    suspend fun getItemById(id: Long): InventoryItem? {
        return inventoryRepository.getItemById(id)
    }
}
