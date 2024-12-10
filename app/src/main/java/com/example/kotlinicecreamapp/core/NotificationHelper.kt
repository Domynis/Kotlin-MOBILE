package com.example.kotlinicecreamapp.core

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.kotlinicecreamapp.MainActivity
import com.example.kotlinicecreamapp.todo.data.IceCream

class NotificationHelper(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "ice_cream_channel"
        private const val CHANNEL_NAME = "IceCream App Notifications"
        private const val CHANNEL_DESCRIPTION = "Notification for IceCream App"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 12 and below, permission is granted by default
            true
        }
    }

    fun showSimpleNotification(
        title: String,
        content: String,
        priority: Int = NotificationManager.IMPORTANCE_HIGH
    ) {
        if (!hasNotificationPermission()) {
            Log.w("NotificationHelper", "Notification permission not granted")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } catch (e: SecurityException) {
            // This catch block provides an extra layer of safety
            Log.e("NotificationHelper", "Failed to show notification", e)
        }
    }

    fun showNetworkStatusNotification(isOnline: Boolean) {
        val title = "Network Status"
        val content = if (isOnline) "Internet connection is back" else "No internet connection"
        showSimpleNotification(title, content)
    }

    fun showUpdateFailedNotification(iceCream: IceCream) {
        val title = "Update Failed"
        val content = "Failed to update the iceCream ${iceCream.name}"
        showSimpleNotification(title, content)
    }

    fun showCreateFailedNotification(iceCream: IceCream) {
        val title = "Create Failed"
        val content = "Failed to create the iceCream ${iceCream.name}"
        showSimpleNotification(title, content)
    }
}