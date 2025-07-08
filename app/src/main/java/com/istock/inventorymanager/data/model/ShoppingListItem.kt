package com.istock.inventorymanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "shopping_list_items",
    foreignKeys = [
        ForeignKey(
            entity = InventoryItem::class,
            parentColumns = ["id"],
            childColumns = ["inventoryItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["inventoryItemId"])]
)
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val inventoryItemId: Long? = null,
    val name: String,
    val quantity: Int,
    val priority: Int = 1, // 1 = Low, 2 = Medium, 3 = High
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val notes: String = ""
)
