package com.henrisusanto.rentipro.feature.home

import com.henrisusanto.rentipro.core.database.entity.RentalEntity
import com.henrisusanto.rentipro.core.database.model.ActiveRentalWithUnit
import com.henrisusanto.rentipro.core.timer.RentalTimer
import com.henrisusanto.rentipro.core.timer.RentalTimerPhase

object HomeRentalMapper {

    fun categorize(
        rental: RentalEntity,
        unitName: String,
        dueSoonMinutes: Int,
        nowMillis: Long,
    ): HomeRentalItem {
        val snapshot = RentalTimer.snapshot(rental, nowMillis, dueSoonMinutes)
        val category = when {
            snapshot.phase == RentalTimerPhase.OVERDUE -> HomeRentalCategory.OVERDUE
            snapshot.isDueSoon -> HomeRentalCategory.DUE_SOON
            else -> HomeRentalCategory.RENTED
        }
        return HomeRentalItem(
            rentalId = rental.id,
            unitId = rental.unitId,
            unitName = unitName,
            price = rental.price,
            isPaused = rental.isPaused,
            timerSnapshot = snapshot,
            category = category,
        )
    }

    fun buildActiveSections(
        activeRentals: List<ActiveRentalWithUnit>,
        dueSoonMinutes: Int,
        nowMillis: Long,
    ): Triple<List<HomeRentalItem>, List<HomeRentalItem>, List<HomeRentalItem>> {
        val overdue = mutableListOf<HomeRentalItem>()
        val dueSoon = mutableListOf<HomeRentalItem>()
        val rented = mutableListOf<HomeRentalItem>()

        activeRentals.forEach { item ->
            val homeItem = categorize(item.rental, item.unit.name, dueSoonMinutes, nowMillis)
            when (homeItem.category) {
                HomeRentalCategory.OVERDUE -> overdue += homeItem
                HomeRentalCategory.DUE_SOON -> dueSoon += homeItem
                HomeRentalCategory.RENTED -> rented += homeItem
            }
        }

        return Triple(overdue, dueSoon, rented)
    }
}
