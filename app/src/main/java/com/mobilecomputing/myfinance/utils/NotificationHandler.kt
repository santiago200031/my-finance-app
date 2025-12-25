package com.mobilecomputing.myfinance.utils

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.mobilecomputing.myfinance.R

object NotificationHandler {
    private const val CHANNEL_ID = "reminder_channel"
    private const val CHANNEL_NAME = "Contract Reminders"

    fun createNotificationChannel(context: Context) {
        val channel =
                NotificationChannel(
                                CHANNEL_ID,
                                CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_DEFAULT
                        )
                        .apply { description = "Notifications for contract reminders" }
        val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, title: String, message: String) {
        val builder =
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, builder.build())
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleNotification(
        context: Context,
        reminderId: String,
        timestamp: Long,
        title: String,
        message: String = "Reminder for your contract"
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("id", reminderId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            reminderId.hashCode(), 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (alarmManager.canScheduleExactAlarms()) {
                 alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timestamp,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timestamp,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
