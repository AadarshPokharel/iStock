package com.istock.inventorymanager.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Schedule notifications after device reboot
                scheduleNotificationChecks(context)
            }
        }
    }

    private fun scheduleNotificationChecks(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
