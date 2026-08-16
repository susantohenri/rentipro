package com.henrisusanto.rentipro.core.timer

import com.henrisusanto.rentipro.core.database.entity.RentalEntity
import com.henrisusanto.rentipro.core.model.RentalStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RentalTimerTest {

    @Test
    fun remaining_countsDownFromScheduledEnd() {
        val rental = activeRental(
            startedAt = 0L,
            scheduledEndAt = 30 * 60_000L,
        )
        assertEquals(15 * 60_000L, RentalTimer.remainingMillis(rental, 15 * 60_000L))
        assertEquals("15:00", RentalTimer.snapshot(rental, 15 * 60_000L).formattedTime)
    }

    @Test
    fun overdue_countsUpAfterScheduledEnd() {
        val rental = activeRental(
            startedAt = 0L,
            scheduledEndAt = 30 * 60_000L,
        )
        val now = 30 * 60_000L + 2 * 60_000L + 31_000L
        val snapshot = RentalTimer.snapshot(rental, now, dueSoonMinutes = 5)

        assertTrue(snapshot.phase == RentalTimerPhase.OVERDUE)
        assertEquals(2 * 60_000L + 31_000L, snapshot.displayMillis)
        assertEquals("02:31", snapshot.formattedTime)
    }

    @Test
    fun paused_freezesRemaining_andIsNotOverdue() {
        val rental = activeRental(
            startedAt = 0L,
            scheduledEndAt = 30 * 60_000L,
            isPaused = true,
            pausedAt = 20 * 60_000L,
        )
        val now = 60 * 60_000L

        assertEquals(10 * 60_000L, RentalTimer.remainingMillis(rental, now))
        assertFalse(RentalTimer.isOverdue(rental, now))

        val snapshot = RentalTimer.snapshot(rental, now)
        assertEquals(RentalTimerPhase.PAUSED, snapshot.phase)
        assertEquals("10:00", snapshot.formattedTime)
    }

    @Test
    fun extendedRental_usesUpdatedScheduledEnd() {
        val rental = activeRental(
            startedAt = 0L,
            scheduledEndAt = 45 * 60_000L,
            durationMinutes = 45,
        )
        val now = 30 * 60_000L

        assertFalse(RentalTimer.isOverdue(rental, now))
        assertEquals(15 * 60_000L, RentalTimer.remainingMillis(rental, now))
    }

    @Test
    fun dueSoon_withinThreshold_onlyWhenActive() {
        val rental = activeRental(
            startedAt = 0L,
            scheduledEndAt = 30 * 60_000L,
        )
        val fourMinutesRemaining = 30 * 60_000L - 4 * 60_000L

        assertTrue(RentalTimer.isDueSoon(rental, dueSoonMinutes = 5, nowMillis = fourMinutesRemaining))
        assertTrue(
            RentalTimer.snapshot(rental, fourMinutesRemaining, dueSoonMinutes = 5).isDueSoon,
        )

        val sixMinutesRemaining = 30 * 60_000L - 6 * 60_000L
        assertFalse(RentalTimer.isDueSoon(rental, dueSoonMinutes = 5, nowMillis = sixMinutesRemaining))
    }

    @Test
    fun backgroundResume_usesCurrentClock_notElapsedUiTicks() {
        val rental = activeRental(
            startedAt = 1_000L,
            scheduledEndAt = 1_000L + 20 * 60_000L,
        )
        val afterBackground = 1_000L + 5 * 60_000L

        assertEquals(15 * 60_000L, RentalTimer.remainingMillis(rental, afterBackground))
    }

    private fun activeRental(
        startedAt: Long,
        scheduledEndAt: Long,
        durationMinutes: Int = 30,
        price: Int = 10,
        isPaused: Boolean = false,
        pausedAt: Long? = null,
    ): RentalEntity = RentalEntity(
        id = 1,
        unitId = 1,
        presetId = 1,
        durationMinutes = durationMinutes,
        price = price,
        startedAt = startedAt,
        scheduledEndAt = scheduledEndAt,
        status = RentalStatus.ACTIVE,
        isPaused = isPaused,
        pausedAt = pausedAt,
    )
}
