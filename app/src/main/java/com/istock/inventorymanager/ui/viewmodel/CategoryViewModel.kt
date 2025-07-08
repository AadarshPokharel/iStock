package com.istock.inventorymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istock.inventorymanager.data.model.Category
import com.istock.inventorymanager.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CategoryViewModel @Inject constructor(private val categoryRepository: CategoryRepository) :
        ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categoryList ->
                _categories.value = categoryList
            }
        }
    }

    fun addCategory(name: String, description: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val category = Category(name = name, description = description)
                categoryRepository.insertCategory(category)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                categoryRepository.updateCategory(category)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                categoryRepository.deleteCategory(category)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
