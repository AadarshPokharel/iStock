package com.istock.inventorymanager.data.model

import java.util.Date

data class Notification(
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Date = Date(),
    val isRead: Boolean = false,
    val itemId: Long? = null // Reference to the related inventory item if applicable
)

enum class NotificationType {
    LOW_STOCK,
    EXPIRING_SOON,
    WARRANTY_EXPIRING,
    ITEM_UPDATED,
    ITEM_DELETED,
    GENERAL
}
