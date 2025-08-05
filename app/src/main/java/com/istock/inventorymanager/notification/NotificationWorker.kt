package com.istock.inventorymanager.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.istock.inventorymanager.MainActivity
import com.istock.inventorymanager.R
import com.istock.inventorymanager.data.database.InventoryDatabase
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.istock.inventorymanager.data.model.NotificationEntity
import com.istock.inventorymanager.data.model.NotificationType
import com.istock.inventorymanager.data.repository.NotificationRepository
import kotlinx.coroutines.flow.first

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
        CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "inventory_notifications"
        const val LOW_STOCK_NOTIFICATION_ID = 1
        const val EXPIRY_NOTIFICATION_ID = 2
        const val WARRANTY_NOTIFICATION_ID = 3
    }

    override suspend fun doWork(): Result =
            withContext(Dispatchers.IO) {
                try {
                    val database = InventoryDatabase.getDatabase(applicationContext)
                    val inventoryDao = database.inventoryItemDao()

                    val notificationDao = database.notificationDao()
                    val notificationRepository = NotificationRepository(notificationDao)

                    // Check for low stock items
                    val lowStockItems = inventoryDao.getLowStockItems().first()
                    if (lowStockItems.isNotEmpty()) {
                        showLowStockNotification(lowStockItems.size)
                        notificationRepository.insert(
                            NotificationEntity(
                                id = System.currentTimeMillis(),
                                title = "Low Stock Alert",
                                message = "${lowStockItems.size} item${if (lowStockItems.size > 1) "s are" else " is"} running low on stock",
                                type = NotificationType.LOW_STOCK,
                                timestamp = Date(),
                                isRead = false,
                                itemId = null
                            )
                        )
                    }


                    // Check for expiring items (within 7 days)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_MONTH, 7)
                    val weekFromNow = calendar.time

                    val expiringItems = inventoryDao.getExpiringItems(weekFromNow)
                    if (expiringItems.isNotEmpty()) {
                        showExpiryNotification(expiringItems.size)
                        notificationRepository.insert(
                            NotificationEntity(
                                id = System.currentTimeMillis(),
                                title = "Items Expiring Soon",
                                message = "${expiringItems.size} item${if (expiringItems.size > 1) "s expire" else " expires"} within 7 days",
                                type = NotificationType.EXPIRING_SOON,
                                timestamp = Date(),
                                isRead = false,
                                itemId = null
                            )
                        )
                    }

                    // Check for warranty expiring items (within 30 days)
                    calendar.time = Date()
                    calendar.add(Calendar.DAY_OF_MONTH, 30)
                    val monthFromNow = calendar.time
                    val warrantyExpiringItems = inventoryDao.getWarrantyExpiringItems(monthFromNow)
                    if (warrantyExpiringItems.isNotEmpty()) {
                        showWarrantyNotification(warrantyExpiringItems.size)
                        notificationRepository.insert(
                            NotificationEntity(
                                id = System.currentTimeMillis(),
                                title = "Warranties Expiring Soon",
                                message = "${warrantyExpiringItems.size} item${if (warrantyExpiringItems.size > 1) " warranties expire" else " warranty expires"} within 30 days",
                                type = NotificationType.WARRANTY_EXPIRING,
                                timestamp = Date(),
                                isRead = false,
                                itemId = null
                            )
                        )
                    }

                    Result.success()
                } catch (e: Exception) {
                    Result.failure()
                }
            }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                    NotificationChannel(
                                    CHANNEL_ID,
                                    "Inventory Notifications",
                                    NotificationManager.IMPORTANCE_DEFAULT
                            )
                            .apply { description = "Notifications for inventory management" }

            val notificationManager =
                    applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showLowStockNotification(count: Int) {
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent =
                PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        val notification =
                NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Low Stock Alert")
                        .setContentText(
                                "$count item${if (count > 1) "s are" else " is"} running low on stock"
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

        val notificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.notify(LOW_STOCK_NOTIFICATION_ID, notification)
    }

    private fun showExpiryNotification(count: Int) {
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent =
                PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        val notification =
                NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Items Expiring Soon")
                        .setContentText(
                                "$count item${if (count > 1) "s expire" else " expires"} within 7 days"
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

        val notificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.notify(EXPIRY_NOTIFICATION_ID, notification)
    }

    private fun showWarrantyNotification(count: Int) {
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent =
                PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        val notification =
                NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Warranties Expiring Soon")
                        .setContentText(
                                "$count item${if (count > 1) " warranties expire" else " warranty expires"} within 30 days"
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

        val notificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.notify(WARRANTY_NOTIFICATION_ID, notification)
    }
}
