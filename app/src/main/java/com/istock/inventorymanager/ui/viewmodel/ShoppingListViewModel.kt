package com.istock.inventorymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istock.inventorymanager.data.model.ShoppingListItem
import com.istock.inventorymanager.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ShoppingListViewModel
@Inject
constructor(private val shoppingListRepository: ShoppingListRepository) : ViewModel() {

    private val _shoppingItems = MutableStateFlow<List<ShoppingListItem>>(emptyList())
    val shoppingItems: StateFlow<List<ShoppingListItem>> = _shoppingItems.asStateFlow()

    private val _pendingItems = MutableStateFlow<List<ShoppingListItem>>(emptyList())
    val pendingItems: StateFlow<List<ShoppingListItem>> = _pendingItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadShoppingItems()
        loadPendingItems()
    }

    private fun loadShoppingItems() {
        viewModelScope.launch {
            shoppingListRepository.getAllShoppingItems().collect { items ->
                _shoppingItems.value = items
            }
        }
    }

    private fun loadPendingItems() {
        viewModelScope.launch {
            shoppingListRepository.getPendingShoppingItems().collect { items ->
                _pendingItems.value = items
            }
        }
    }

    fun addShoppingItem(
            name: String,
            quantity: Int,
            priority: Int = 1,
            notes: String = "",
            inventoryItemId: Long? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val item =
                        ShoppingListItem(
                                name = name,
                                quantity = quantity,
                                priority = priority,
                                notes = notes,
                                inventoryItemId = inventoryItemId
                        )
                shoppingListRepository.insertShoppingItem(item)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateShoppingItem(item: ShoppingListItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.updateShoppingItem(item)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleCompletion(itemId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.updateCompletionStatus(itemId, isCompleted)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteShoppingItem(item: ShoppingListItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.deleteShoppingItem(item)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCompletedItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.deleteCompletedItems()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
