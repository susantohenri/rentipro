package com.henrisusanto.rentipro.feature.rental

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity
import com.henrisusanto.rentipro.core.util.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartRentalBottomSheet(
    state: StartRentalUiState,
    onDismiss: () -> Unit,
    onUnitSelected: (Long) -> Unit,
    onPresetSelected: (Long) -> Unit,
    onBackToUnits: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            when {
                state.error != null -> ErrorContent(error = state.error)
                state.step == StartRentalStep.SELECT_UNIT -> UnitSelectionContent(
                    units = state.availableUnits,
                    onUnitSelected = onUnitSelected,
                )
                state.step == StartRentalStep.SELECT_PRESET -> PresetSelectionContent(
                    unitName = state.selectedUnitName.orEmpty(),
                    presets = state.presets,
                    isStarting = state.isStarting,
                    showBack = state.availableUnits.size > 1,
                    onBack = onBackToUnits,
                    onPresetSelected = onPresetSelected,
                )
            }
        }
    }
}

@Composable
private fun UnitSelectionContent(
    units: List<RentalUnitEntity>,
    onUnitSelected: (Long) -> Unit,
) {
    Text(
        text = stringResource(R.string.start_rental_select_unit),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    )
    LazyColumn {
        items(units, key = { it.id }) { unit ->
            ListItem(
                headlineContent = { Text(unit.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUnitSelected(unit.id) },
            )
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetSelectionContent(
    unitName: String,
    presets: List<RentalPresetEntity>,
    isStarting: Boolean,
    showBack: Boolean,
    onBack: () -> Unit,
    onPresetSelected: (Long) -> Unit,
) {
    if (showBack) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
            )
        }
    }
    Text(
        text = stringResource(R.string.start_rental_select_preset, unitName),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
    )
    if (isStarting) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        )
    } else {
        LazyColumn {
            items(presets, key = { it.id }) { preset ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(
                                R.string.rental_preset_format,
                                preset.durationMinutes,
                                preset.price.toFormattedString(),
                            ),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPresetSelected(preset.id) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ErrorContent(error: StartRentalError) {
    val messageRes = when (error) {
        StartRentalError.NO_UNITS -> R.string.start_rental_no_units
        StartRentalError.NO_PRESETS -> R.string.start_rental_no_presets
    }
    Text(
        text = stringResource(messageRes),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(24.dp),
    )
}
