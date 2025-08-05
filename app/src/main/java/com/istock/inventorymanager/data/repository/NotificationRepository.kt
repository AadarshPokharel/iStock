package com.istock.inventorymanager.data.repository

import com.istock.inventorymanager.data.dao.NotificationDao
import com.istock.inventorymanager.data.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val dao: NotificationDao) {
    fun getAllNotifications(): Flow<List<NotificationEntity>> = dao.getAllNotifications()
    suspend fun insert(notification: NotificationEntity) = dao.insert(notification)
    suspend fun insertAll(notifications: List<NotificationEntity>) = dao.insertAll(notifications)
    suspend fun clearAll() = dao.clearAll()
    suspend fun markAsRead(notificationId: Long) = dao.markAsRead(notificationId)
}