package com.istock.inventorymanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istock.inventorymanager.data.dao.InventoryItemDao
import com.istock.inventorymanager.data.model.InventoryItem
import com.istock.inventorymanager.data.model.Notification
import com.istock.inventorymanager.data.model.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val itemDao: InventoryItemDao
) : ViewModel() {

    companion object {
        private const val EXP_SOON_DAYS = 7
        private const val WARRANTY_SOON_DAYS = 30
        private val DAY_MS: Long = TimeUnit.DAYS.toMillis(1)
        private val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    }

    // Keep track of dismissed notifications (per item + type) in memory.
    private val dismissed = MutableStateFlow<Set<Pair<Long, NotificationType>>>(emptySet())

    // 1) Low stock comes straight from DAO.
    private val lowStockFlow = itemDao.getLowStockItems().map { items ->
        items.map { it.toNotification(NotificationType.LOW_STOCK) }
    }

    // 2) Expiring soon: derive from all items.
    private val expiringSoonFlow = itemDao.getAllItems().map { items ->
        val cutoff = Date(System.currentTimeMillis() + EXP_SOON_DAYS * DAY_MS)
        items.filter { it.expirationDate?.let { d -> d <= cutoff } == true }
            .map { it.toNotification(NotificationType.EXPIRING_SOON) }
    }

    // 3) Warranty expiring soon: derive from all items.
    private val warrantySoonFlow = itemDao.getAllItems().map { items ->
        val cutoff = Date(System.currentTimeMillis() + WARRANTY_SOON_DAYS * DAY_MS)
        items.filter { it.warrantyDate?.let { d -> d <= cutoff } == true }
            .map { it.toNotification(NotificationType.WARRANTY_EXPIRING) }
    }

    // Combine all three streams into one.
    val notifications: StateFlow<List<Notification>> =
        combine(lowStockFlow, expiringSoonFlow, warrantySoonFlow, dismissed) {
                low, exp, war, dismissedSet ->
            (low + exp + war)
                .distinctBy { it.id to it.type }
                .filter { (it.id to it.type) !in dismissedSet }
                .sortedByDescending { it.timestamp.time }
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Called when a notification card is tapped. Marks as read/dismissed in-memory. */
    fun onClick(n: Notification) = viewModelScope.launch {
        dismissed.update { it + (n.id to n.type) }
    }

    fun markAsRead(itemId: Long) = viewModelScope.launch {
        // dismiss ALL notification types for this item id
        dismissed.update { set -> set + NotificationType.values().map { t -> itemId to t } }
    }

    fun clearAll() = onClearAll()


    /** Clear/hide all currently visible notifications (non-destructive). */
    fun onClearAll() = viewModelScope.launch {
        val curr = notifications.value
        if (curr.isNotEmpty()) {
            dismissed.update { it + curr.map { n -> n.id to n.type }.toSet() }
        }
    }


    private fun InventoryItem.toNotification(type: NotificationType): Notification {
        val title = when (type) {
            NotificationType.LOW_STOCK        -> "Low stock: $name"
            NotificationType.EXPIRING_SOON    -> "Expiring soon: $name"
            NotificationType.WARRANTY_EXPIRING-> "Warranty expiring: $name"
            NotificationType.ITEM_UPDATED     -> "Item updated: $name"
            NotificationType.ITEM_DELETED     -> "Item deleted: $name"
            NotificationType.GENERAL          -> "Notice: $name"
        }

        val message = when (type) {
            NotificationType.LOW_STOCK ->
                "Only $quantity left (min $minStockLevel)."
            NotificationType.EXPIRING_SOON ->
                "Expires on ${expirationDate?.let { dateFmt.format(it) } ?: "unknown"}."
            NotificationType.WARRANTY_EXPIRING ->
                "Warranty ends on ${warrantyDate?.let { dateFmt.format(it) } ?: "unknown"}."
            else -> ""
        }

        return Notification(
            id = id,
            title = title,
            message = message,
            timestamp = Date(),
            type = type,
            isRead = false
        )
    }
}
