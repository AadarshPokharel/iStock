package com.istock.inventorymanager.data.dao

import androidx.room.*
import com.istock.inventorymanager.data.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_list_items ORDER BY priority DESC, createdAt ASC")
    fun getAllShoppingItems(): Flow<List<ShoppingListItem>>

    @Query(
            "SELECT * FROM shopping_list_items WHERE isCompleted = 0 ORDER BY priority DESC, createdAt ASC"
    )
    fun getPendingShoppingItems(): Flow<List<ShoppingListItem>>

    @Query("SELECT * FROM shopping_list_items WHERE id = :id")
    suspend fun getShoppingItemById(id: Long): ShoppingListItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingListItem): Long

    @Update suspend fun updateShoppingItem(item: ShoppingListItem)

    @Delete suspend fun deleteShoppingItem(item: ShoppingListItem)

    @Query("DELETE FROM shopping_list_items WHERE id = :id")
    suspend fun deleteShoppingItemById(id: Long)

    @Query("UPDATE shopping_list_items SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)

    @Query("DELETE FROM shopping_list_items WHERE isCompleted = 1")
    suspend fun deleteCompletedItems()
}
