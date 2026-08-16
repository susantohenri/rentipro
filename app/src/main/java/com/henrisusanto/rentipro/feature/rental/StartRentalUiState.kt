package com.henrisusanto.rentipro.feature.rental

import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity

enum class StartRentalStep {
    SELECT_UNIT,
    SELECT_PRESET,
}

enum class StartRentalError {
    NO_UNITS,
    NO_PRESETS,
}

data class StartRentalUiState(
    val isVisible: Boolean = false,
    val step: StartRentalStep = StartRentalStep.SELECT_UNIT,
    val availableUnits: List<RentalUnitEntity> = emptyList(),
    val presets: List<RentalPresetEntity> = emptyList(),
    val selectedUnitId: Long? = null,
    val selectedUnitName: String? = null,
    val isStarting: Boolean = false,
    val error: StartRentalError? = null,
)
