package com.henrisusanto.rentipro.feature.items

import com.henrisusanto.rentipro.core.model.UnitStatus
import com.henrisusanto.rentipro.core.timer.RentalTimerSnapshot

data class ItemRow(
    val unitId: Long,
    val name: String,
    val status: UnitStatus,
    val timerSnapshot: RentalTimerSnapshot?,
    val rentalId: Long?,
)

data class ItemsUiState(
    val items: List<ItemRow> = emptyList(),
)
