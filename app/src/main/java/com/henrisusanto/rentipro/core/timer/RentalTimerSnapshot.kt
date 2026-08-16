package com.henrisusanto.rentipro.core.timer

import com.henrisusanto.rentipro.core.util.TimeFormatter

enum class RentalTimerPhase {
    REMAINING,
    OVERDUE,
    PAUSED,
}

/**
 * Immutable snapshot of rental timer state at a specific [nowMillis].
 * All display values are derived from persisted timestamps — never from a decrementing counter.
 */
data class RentalTimerSnapshot(
    val phase: RentalTimerPhase,
    val displayMillis: Long,
    val isDueSoon: Boolean,
    val formattedTime: String,
)
