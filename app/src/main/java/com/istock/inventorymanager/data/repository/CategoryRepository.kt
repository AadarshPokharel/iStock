package com.istock.inventorymanager.data.repository

import com.istock.inventorymanager.data.dao.CategoryDao
import com.istock.inventorymanager.data.model.Category
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CategoryRepository @Inject constructor(private val categoryDao: CategoryDao) {

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    suspend fun deleteCategoryById(id: Long) = categoryDao.deleteCategoryById(id)
}
