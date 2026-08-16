package com.henrisusanto.rentipro.core.timer

import com.henrisusanto.rentipro.core.database.entity.RentalEntity
import com.henrisusanto.rentipro.core.util.TimeFormatter
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

object RentalTimer {

    fun remainingMillis(rental: RentalEntity, nowMillis: Long): Long {
        if (rental.isPaused && rental.pausedAt != null) {
            return (rental.scheduledEndAt - rental.pausedAt).coerceAtLeast(0)
        }
        return (rental.scheduledEndAt - nowMillis).coerceAtLeast(0)
    }

    fun overdueMillis(rental: RentalEntity, nowMillis: Long): Long {
        if (rental.isPaused) return 0
        return (nowMillis - rental.scheduledEndAt).coerceAtLeast(0)
    }

    fun isOverdue(rental: RentalEntity, nowMillis: Long): Boolean {
        if (rental.isPaused) return false
        return nowMillis >= rental.scheduledEndAt
    }

    fun isDueSoon(
        rental: RentalEntity,
        dueSoonMinutes: Int,
        nowMillis: Long,
    ): Boolean {
        if (rental.isPaused || isOverdue(rental, nowMillis)) return false
        val thresholdMillis = dueSoonMinutes * 60_000L
        val remaining = remainingMillis(rental, nowMillis)
        return remaining in 1..thresholdMillis
    }

    fun snapshot(
        rental: RentalEntity,
        nowMillis: Long,
        dueSoonMinutes: Int = 0,
    ): RentalTimerSnapshot {
        val paused = rental.isPaused
        val overdue = isOverdue(rental, nowMillis)
        val phase = when {
            paused -> RentalTimerPhase.PAUSED
            overdue -> RentalTimerPhase.OVERDUE
            else -> RentalTimerPhase.REMAINING
        }
        val displayMillis = when (phase) {
            RentalTimerPhase.OVERDUE -> overdueMillis(rental, nowMillis)
            else -> remainingMillis(rental, nowMillis)
        }
        return RentalTimerSnapshot(
            phase = phase,
            displayMillis = displayMillis,
            isDueSoon = !paused && !overdue && isDueSoon(rental, dueSoonMinutes, nowMillis),
            formattedTime = TimeFormatter.formatCountdown(displayMillis),
        )
    }
}

object TimerTicker {

    private const val DEFAULT_INTERVAL_MS = 1_000L

    /**
     * Emits [System.currentTimeMillis] on each tick.
     * Survives background/foreground transitions because each emission re-reads the clock.
     */
    fun currentTimeMillis(intervalMillis: Long = DEFAULT_INTERVAL_MS): Flow<Long> = flow {
        while (currentCoroutineContext().isActive) {
            emit(System.currentTimeMillis())
            delay(intervalMillis)
        }
    }
}
