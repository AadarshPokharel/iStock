package com.istock.inventorymanager.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.istock.inventorymanager.data.model.Notification
import com.istock.inventorymanager.data.model.NotificationType
import com.istock.inventorymanager.ui.theme.warning
import com.istock.inventorymanager.ui.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* Route/Host: collect state from VM */
@Composable
fun NotificationsRoute(
    vm: NotificationViewModel = hiltViewModel()
) {
    val notifications by vm.notifications.collectAsStateWithLifecycle()
    NotificationScreen(
        notifications = notifications,
        onNotificationClick = vm::onClick,
        onClearAll = vm::onClearAll
    )
}

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
                title = {
                    Text(
                        "Notifications",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 30.sp, fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = onClearAll) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear all")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color(0xFFFDF3DA),
                    actionIconContentColor = Color(0xFFFDF3DA)
                )
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
                    Spacer(Modifier.height(8.dp))
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
                items(
                    items = notifications,
                    key = { n -> "${n.type}-${n.id}" } // stable key per item+type
                ) { n ->
                    NotificationItem(notification = n, onClick = { onNotificationClick(n) })
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationIcon(type = notification.type)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
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
                    text = formatTimestamp(notification.timestamp, dateFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!notification.isRead) {
                Box(
                    modifier = Modifier.size(8.dp).background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                )
            }
        }
    }
}

private fun formatTimestamp(ts: Any, formatter: SimpleDateFormat): String =
    when (ts) {
        is Date -> formatter.format(ts)
        is Long -> formatter.format(Date(ts))
        else -> ""
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
    Icon(imageVector = icon, contentDescription = type.name, tint = color, modifier = Modifier.size(24.dp))
}
