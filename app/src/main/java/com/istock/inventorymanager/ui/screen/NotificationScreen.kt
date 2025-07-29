package com.istock.inventorymanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.istock.inventorymanager.data.model.Notification
import com.istock.inventorymanager.data.model.NotificationType
import com.istock.inventorymanager.ui.theme.warning
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit,
    onClearAll: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                actions = {
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = onClearAll) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear all notifications")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No notifications",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(notifications.sortedByDescending { it.timestamp }) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { onNotificationClick(notification) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationIcon(type = notification.type)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateFormatter.format(notification.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun NotificationIcon(type: NotificationType) {
    val icon = when (type) {
        NotificationType.LOW_STOCK -> Icons.Default.Warning
        NotificationType.EXPIRING_SOON -> Icons.Default.Timer
        NotificationType.WARRANTY_EXPIRING -> Icons.Default.Security
        NotificationType.ITEM_UPDATED -> Icons.Default.Edit
        NotificationType.ITEM_DELETED -> Icons.Default.Delete
        NotificationType.GENERAL -> Icons.Default.Notifications
    }
    
    val color = when (type) {
        NotificationType.LOW_STOCK -> MaterialTheme.colorScheme.error
        NotificationType.EXPIRING_SOON -> MaterialTheme.colorScheme.error
        NotificationType.WARRANTY_EXPIRING -> MaterialTheme.colorScheme.warning
        NotificationType.ITEM_UPDATED -> MaterialTheme.colorScheme.primary
        NotificationType.ITEM_DELETED -> MaterialTheme.colorScheme.error
        NotificationType.GENERAL -> MaterialTheme.colorScheme.primary
    }
    
    Icon(
        imageVector = icon,
        contentDescription = type.name,
        tint = color,
        modifier = Modifier.size(24.dp)
    )
}
