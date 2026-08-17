package com.henrisusanto.rentipro.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.henrisusanto.rentipro.MainActivity
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.core.alarm.AlarmIds

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showDueSoon(rentalId: Long, unitName: String, dueSoonMinutes: Int) {
        show(
            notificationId = AlarmIds.dueSoonNotificationId(rentalId),
            title = context.getString(R.string.notification_due_soon_title),
            text = context.getString(R.string.notification_due_soon_text, unitName, dueSoonMinutes),
        )
    }

    fun showOverdue(rentalId: Long, unitName: String) {
        show(
            notificationId = AlarmIds.overdueNotificationId(rentalId),
            title = context.getString(R.string.notification_overdue_title),
            text = context.getString(R.string.notification_overdue_text, unitName),
        )
    }

    private fun show(notificationId: Int, title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID = "rental_reminders"
    }
}
