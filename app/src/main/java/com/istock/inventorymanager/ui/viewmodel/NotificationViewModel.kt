package com.istock.inventorymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istock.inventorymanager.data.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    init {
        // Add a test notification for verification
        addNotification(
            Notification(
                id = 1,
                title = "Test Notification",
                message = "This is a test notification. If you see this, notifications are working!",
                type = com.istock.inventorymanager.data.model.NotificationType.GENERAL,
                timestamp = Date(),
                isRead = false
            )
        )
    }

    fun addNotification(notification: Notification) {
        _notifications.value = listOf(notification) + _notifications.value
    }

    fun clearAll() {
        _notifications.value = emptyList()
    }

    fun markAsRead(notificationId: Long) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }
}
