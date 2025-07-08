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
import kotlinx.coroutines.launch

@HiltViewModel
class SimpleInventoryViewModel @Inject constructor(private val inventoryRepository: InventoryRepository) :
        ViewModel() {

    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    init {
        loadAllItems()
    }

    private fun loadAllItems() {
        viewModelScope.launch {
            try {
                inventoryRepository.getAllItems().collect { items ->
                    _inventoryItems.value = items
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // In case of error, still stop loading
                _isLoading.value = false
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
}
