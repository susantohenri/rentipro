package com.henrisusanto.rentipro.feature.rental

import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import com.henrisusanto.rentipro.feature.home.HomeRentalItem

data class ActiveRentalSheetUiState(
    val item: HomeRentalItem? = null,
    val showDeleteConfirmation: Boolean = false,
    val isProcessing: Boolean = false,
    val showExtendSelection: Boolean = false,
    val presets: List<RentalPresetEntity> = emptyList(),
)
