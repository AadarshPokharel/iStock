package com.istock.inventorymanager.data.repository

import com.istock.inventorymanager.data.dao.ShoppingListDao
import com.istock.inventorymanager.data.model.ShoppingListItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ShoppingListRepository @Inject constructor(private val shoppingListDao: ShoppingListDao) {

    fun getAllShoppingItems(): Flow<List<ShoppingListItem>> = shoppingListDao.getAllShoppingItems()

    fun getPendingShoppingItems(): Flow<List<ShoppingListItem>> =
            shoppingListDao.getPendingShoppingItems()

    suspend fun getShoppingItemById(id: Long): ShoppingListItem? =
            shoppingListDao.getShoppingItemById(id)

    suspend fun insertShoppingItem(item: ShoppingListItem): Long =
            shoppingListDao.insertShoppingItem(item)

    suspend fun updateShoppingItem(item: ShoppingListItem) =
            shoppingListDao.updateShoppingItem(item)

    suspend fun deleteShoppingItem(item: ShoppingListItem) =
            shoppingListDao.deleteShoppingItem(item)

    suspend fun deleteShoppingItemById(id: Long) = shoppingListDao.deleteShoppingItemById(id)

    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean) =
            shoppingListDao.updateCompletionStatus(id, isCompleted)

    suspend fun deleteCompletedItems() = shoppingListDao.deleteCompletedItems()
}
