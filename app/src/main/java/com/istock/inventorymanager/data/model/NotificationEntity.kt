package com.istock.inventorymanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Date,
    val isRead: Boolean,
    val itemId: Long?
)