package com.istock.inventorymanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "inventory_items",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val categoryId: Long,
    val quantity: Int,
    val minStockLevel: Int = 0,
    val expirationDate: Date? = null,
    val warrantyDate: Date? = null,
    val price: Double = 0.0,
    val imagePath: String? = null,
    val barcode: String? = null,
    val location: String = "",
    val notes: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
