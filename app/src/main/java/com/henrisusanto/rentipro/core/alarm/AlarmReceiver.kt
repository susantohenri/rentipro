package com.henrisusanto.rentipro.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.henrisusanto.rentipro.RentiproApplication
import com.henrisusanto.rentipro.core.database.entity.RentalEntity
import com.henrisusanto.rentipro.core.model.RentalStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receives exact-alarm intents for active rentals and posts the matching
 * notification. Guards with the [RentalEntity.dueSoonNotified] /
 * [RentalEntity.overdueNotified] flags so each reminder fires only once.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val rentalId = intent.getLongExtra(AlarmIds.EXTRA_RENTAL_ID, AlarmIds.NO_ID)
        val type = intent.getStringExtra(AlarmIds.EXTRA_ALARM_TYPE)
        if (rentalId == AlarmIds.NO_ID || type == null) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                handleAlarm(appContext, rentalId, type)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleAlarm(context: Context, rentalId: Long, type: String) {
        val container = (context as RentiproApplication).container
        val rental = container.rentalRepository.getRental(rentalId) ?: return
        if (rental.status != RentalStatus.ACTIVE || rental.isPaused) return

        val now = System.currentTimeMillis()
        when (type) {
            AlarmIds.TYPE_DUE_SOON -> {
                // Skip if already notified, or the rental already ended (overdue alarm handles it).
                if (rental.dueSoonNotified || now >= rental.scheduledEndAt) return
                val unit = container.unitRepository.getUnit(rental.unitId) ?: return
                val dueSoonMinutes = container.settingsRepository.dueSoonMinutes.first()
                container.notificationHelper.showDueSoon(rental.id, unit.name, dueSoonMinutes)
                container.rentalRepository.updateRental(rental.copy(dueSoonNotified = true))
            }
            AlarmIds.TYPE_OVERDUE -> {
                if (rental.overdueNotified || now < rental.scheduledEndAt) return
                val unit = container.unitRepository.getUnit(rental.unitId) ?: return
                container.notificationHelper.showOverdue(rental.id, unit.name)
                container.rentalRepository.updateRental(rental.copy(overdueNotified = true))
                container.rentalRepository.markUnitOverdue(rental.unitId)
            }
        }
    }
}
