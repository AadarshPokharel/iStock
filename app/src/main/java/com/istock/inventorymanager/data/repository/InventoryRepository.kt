package com.istock.inventorymanager.data.repository

import com.istock.inventorymanager.data.dao.InventoryItemDao
import com.istock.inventorymanager.data.model.InventoryItem
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class InventoryRepository @Inject constructor(private val inventoryItemDao: InventoryItemDao) {

    fun getAllItems(): Flow<List<InventoryItem>> = inventoryItemDao.getAllItems()

    fun getItemsByCategory(categoryId: Long): Flow<List<InventoryItem>> =
            inventoryItemDao.getItemsByCategory(categoryId)

    suspend fun getItemById(id: Long): InventoryItem? = inventoryItemDao.getItemById(id)

    fun getLowStockItems(): Flow<List<InventoryItem>> = inventoryItemDao.getLowStockItems()

    suspend fun getExpiringItems(date: Date): List<InventoryItem> =
            inventoryItemDao.getExpiringItems(date)

    suspend fun getWarrantyExpiringItems(date: Date): List<InventoryItem> =
            inventoryItemDao.getWarrantyExpiringItems(date)

    suspend fun insertItem(item: InventoryItem): Long = inventoryItemDao.insertItem(item)

    suspend fun updateItem(item: InventoryItem) = inventoryItemDao.updateItem(item)

    suspend fun deleteItem(item: InventoryItem) = inventoryItemDao.deleteItem(item)

    suspend fun deleteItemById(id: Long) = inventoryItemDao.deleteItemById(id)

    suspend fun updateQuantity(id: Long, newQuantity: Int) =
            inventoryItemDao.updateQuantity(id, newQuantity, Date())
}
