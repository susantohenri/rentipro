package com.henrisusanto.rentipro.feature.home

import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity
import com.henrisusanto.rentipro.core.timer.RentalTimerSnapshot

data class HomeRentalItem(
    val rentalId: Long,
    val unitId: Long,
    val unitName: String,
    val price: Int,
    val isPaused: Boolean,
    val timerSnapshot: RentalTimerSnapshot,
    val category: HomeRentalCategory,
)

enum class HomeRentalCategory {
    OVERDUE,
    DUE_SOON,
    RENTED,
}

data class HomeUiState(
    val overdue: List<HomeRentalItem> = emptyList(),
    val dueSoon: List<HomeRentalItem> = emptyList(),
    val rented: List<HomeRentalItem> = emptyList(),
    val availableUnits: List<RentalUnitEntity> = emptyList(),
    val todayRentalCount: Int = 0,
    val todayRevenue: Int = 0,
    val hasActiveRentals: Boolean = false,
    val bannerAdUnitId: String = "",
) {
    val activeRentals: List<HomeRentalItem>
        get() = overdue + dueSoon + rented
}
