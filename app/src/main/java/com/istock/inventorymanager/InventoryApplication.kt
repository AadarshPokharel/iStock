package com.istock.inventorymanager

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.istock.inventorymanager.notification.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class InventoryApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        scheduleNotificationWork()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Inventory notifications channel
            val inventoryChannel = NotificationChannel(
                "inventory_notifications",
                "Inventory Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for inventory management including low stock, expiry, and warranty alerts"
            }
            
            notificationManager.createNotificationChannel(inventoryChannel)
        }
    }
    
    private fun scheduleNotificationWork() {
        val notificationWork = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS // Check daily
        ).build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "inventory_notifications",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWork
        )
    }
}
