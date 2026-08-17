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
    val showAddDialog: Boolean = false,
    val addText: String = "",
    val deleteError: String? = null,
    val showRenameDialog: Boolean = false,
    val renameUnitId: Long? = null,
    val renameText: String = "",
    val showDeleteConfirmation: Boolean = false,
    val deleteUnitId: Long? = null,
    val deleteUnitName: String = "",
    val isProcessing: Boolean = false,
)
