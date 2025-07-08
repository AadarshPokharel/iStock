package com.istock.inventorymanager.data.dao

import androidx.room.*
import com.istock.inventorymanager.data.model.InventoryItem
import java.util.Date
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {

    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getItemsByCategory(categoryId: Long): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun getItemById(id: Long): InventoryItem?

    @Query("SELECT * FROM inventory_items WHERE quantity <= minStockLevel")
    fun getLowStockItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE quantity <= minStockLevel")
    suspend fun getLowStockItemsSnapshot(): List<InventoryItem>

    @Query(
            "SELECT * FROM inventory_items WHERE expirationDate IS NOT NULL AND expirationDate <= :date"
    )
    suspend fun getExpiringItems(date: Date): List<InventoryItem>

    @Query("SELECT * FROM inventory_items WHERE warrantyDate IS NOT NULL AND warrantyDate <= :date")
    suspend fun getWarrantyExpiringItems(date: Date): List<InventoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem): Long

    @Update suspend fun updateItem(item: InventoryItem)

    @Delete suspend fun deleteItem(item: InventoryItem)

    @Query("DELETE FROM inventory_items WHERE id = :id") suspend fun deleteItemById(id: Long)

    @Query(
            "UPDATE inventory_items SET quantity = :newQuantity, updatedAt = :updatedAt WHERE id = :id"
    )
    suspend fun updateQuantity(id: Long, newQuantity: Int, updatedAt: Date)
}
