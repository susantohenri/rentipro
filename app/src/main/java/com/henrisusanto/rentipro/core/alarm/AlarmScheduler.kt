package com.henrisusanto.rentipro.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.henrisusanto.rentipro.core.database.entity.RentalEntity

/**
 * Schedules / cancels exact alarms for rental reminders.
 * Due-soon fires at (scheduledEndAt - dueSoonMinutes), overdue fires at scheduledEndAt.
 */
interface AlarmScheduler {

    /**
     * Schedules whatever reminders are still needed for an active rental.
     * Idempotent: due-soon is skipped when already notified, overdue when already
     * notified, everything when the rental is paused.
     */
    fun schedule(rental: RentalEntity, dueSoonMinutes: Int)

    fun cancel(rentalId: Long)
}

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager =
        context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(rental: RentalEntity, dueSoonMinutes: Int) {
        if (rental.isPaused) return
        if (!rental.dueSoonNotified) {
            val dueSoonAt = rental.scheduledEndAt - dueSoonMinutes * 60_000L
            scheduleExact(
                triggerAtMillis = dueSoonAt,
                rentalId = rental.id,
                type = AlarmIds.TYPE_DUE_SOON,
                requestCode = AlarmIds.dueSoonRequestCode(rental.id),
            )
        }
        if (!rental.overdueNotified) {
            scheduleExact(
                triggerAtMillis = rental.scheduledEndAt,
                rentalId = rental.id,
                type = AlarmIds.TYPE_OVERDUE,
                requestCode = AlarmIds.overdueRequestCode(rental.id),
            )
        }
    }

    override fun cancel(rentalId: Long) {
        alarmManager.cancel(buildPendingIntent(rentalId, AlarmIds.TYPE_DUE_SOON, AlarmIds.dueSoonRequestCode(rentalId)))
        alarmManager.cancel(buildPendingIntent(rentalId, AlarmIds.TYPE_OVERDUE, AlarmIds.overdueRequestCode(rentalId)))
    }

    private fun scheduleExact(triggerAtMillis: Long, rentalId: Long, type: String, requestCode: Int) {
        val pendingIntent = buildPendingIntent(rentalId, type, requestCode)
        val canScheduleExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            // SCHEDULE_EXACT_ALARM not granted (Android 12+): fall back to inexact.
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun buildPendingIntent(rentalId: Long, type: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmIds.EXTRA_RENTAL_ID, rentalId)
            putExtra(AlarmIds.EXTRA_ALARM_TYPE, type)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
