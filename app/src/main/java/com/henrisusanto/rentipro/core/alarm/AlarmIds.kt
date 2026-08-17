package com.henrisusanto.rentipro.core.alarm

/**
 * Shared constants + id derivation for rental alarms and notifications.
 * Kept pure so it can be unit tested (colliding AlarmManager request codes
 * would cancel each other's alarms).
 */
object AlarmIds {

    const val NO_ID = -1L

    const val EXTRA_RENTAL_ID = "rental_id"
    const val EXTRA_ALARM_TYPE = "alarm_type"
    const val TYPE_DUE_SOON = "due_soon"
    const val TYPE_OVERDUE = "overdue"

    fun dueSoonRequestCode(rentalId: Long): Int = (rentalId * 10 + 1).toInt()

    fun overdueRequestCode(rentalId: Long): Int = (rentalId * 10 + 2).toInt()

    fun dueSoonNotificationId(rentalId: Long): Int = dueSoonRequestCode(rentalId)

    fun overdueNotificationId(rentalId: Long): Int = overdueRequestCode(rentalId)
}
