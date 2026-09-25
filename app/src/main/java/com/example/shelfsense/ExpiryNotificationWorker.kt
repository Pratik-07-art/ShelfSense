package com.example.shelfsense

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ExpiryNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "shelf_sense_alerts"
        private const val CHANNEL_NAME = "Shelf Sense Alerts"
    }

    override fun doWork(): Result {

        val storageManager =
            StorageManager(applicationContext)

        val items =
            storageManager.getItems()

        val expiredItems =
            items.filter {
                ExpiryUtils.isExpired(it)
            }

        val expiringItems =
            items.filter {
                ExpiryUtils.isExpiringSoon(it)
            }

        val lowStockItems =
            items.filter {
                ExpiryUtils.isLowStock(it)
            }

        createNotificationChannel()

        // Expired notification

        if (expiredItems.isNotEmpty()) {

            val names =
                expiredItems
                    .take(5)
                    .joinToString(", ") {
                        it.name
                    }

            showNotification(
                1001,
                "Expired Items",
                "$names have expired."
            )
        }

        // Expiring soon notification

        if (expiringItems.isNotEmpty()) {

            val names =
                expiringItems
                    .take(5)
                    .joinToString(", ") {
                        it.name
                    }

            showNotification(
                1002,
                "Items Expiring Soon",
                "$names expire within 7 days."
            )
        }

        // Low stock notification

        if (lowStockItems.isNotEmpty()) {

            val names =
                lowStockItems
                    .take(5)
                    .joinToString(", ") {
                        it.name
                    }

            showNotification(
                1003,
                "Low Stock",
                "$names are running low."
            )
        }

        return Result.success()
    }

    private fun createNotificationChannel() {

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.createNotificationChannel(channel)
    }

    private fun showNotification(
        notificationId: Int,
        title: String,
        message: String
    ) {

        if (
            android.os.Build.VERSION.SDK_INT >= 33 &&
            applicationContext.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification =
            NotificationCompat.Builder(
                applicationContext,
                CHANNEL_ID
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(
                notificationId,
                notification
            )
    }
}