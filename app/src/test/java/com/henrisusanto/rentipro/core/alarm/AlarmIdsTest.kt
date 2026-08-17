package com.henrisusanto.rentipro.core.alarm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmIdsTest {

    @Test
    fun dueSoonAndOverdueCodes_differPerRental() {
        for (rentalId in 1L..1_000L) {
            assertNotEquals(
                "due-soon and overdue codes must differ for rental $rentalId",
                AlarmIds.dueSoonRequestCode(rentalId),
                AlarmIds.overdueRequestCode(rentalId),
            )
        }
    }

    @Test
    fun codes_doNotCollideAcrossRentals() {
        val codes = (1L..1_000L).flatMap { id ->
            listOf(AlarmIds.dueSoonRequestCode(id), AlarmIds.overdueRequestCode(id))
        }
        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun notificationIds_followAlarmCodes() {
        assertEquals(
            AlarmIds.dueSoonRequestCode(7L),
            AlarmIds.dueSoonNotificationId(7L),
        )
        assertEquals(
            AlarmIds.overdueRequestCode(7L),
            AlarmIds.overdueNotificationId(7L),
        )
    }

    @Test
    fun codes_areStablePerRental() {
        assertTrue(AlarmIds.dueSoonRequestCode(42L) > 0)
        assertTrue(AlarmIds.overdueRequestCode(42L) > 0)
        assertEquals(AlarmIds.dueSoonRequestCode(42L), AlarmIds.dueSoonRequestCode(42L))
    }
}
