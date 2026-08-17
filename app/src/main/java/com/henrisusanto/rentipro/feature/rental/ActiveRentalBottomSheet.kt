package com.henrisusanto.rentipro.feature.rental

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import com.henrisusanto.rentipro.feature.home.HomeRentalCategory
import com.henrisusanto.rentipro.ui.components.RentalTimerText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRentalBottomSheet(
    state: ActiveRentalSheetUiState,
    onDismiss: () -> Unit,
    onReturn: () -> Unit,
    onExtendRequest: () -> Unit,
    onPauseRequest: () -> Unit,
    onResumeRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    onSelectPresetForExtend: (Long) -> Unit,
    onCancelExtend: () -> Unit,
) {
    val item = state.item ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        if (state.showExtendSelection) {
            ExtendPresetSelectionContent(
                presets = state.presets,
                isProcessing = state.isProcessing,
                onBack = onCancelExtend,
                onPresetSelected = onSelectPresetForExtend,
            )
        } else {
            ActiveRentalActionsContent(
                item = item,
                isProcessing = state.isProcessing,
                onReturn = onReturn,
                onExtend = onExtendRequest,
                onPause = onPauseRequest,
                onResume = onResumeRequest,
                onDelete = onDeleteRequest,
            )
        }
    }

    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = onDeleteCancel,
            title = { Text(stringResource(R.string.delete_rental_title)) },
            text = { Text(stringResource(R.string.delete_rental_message, item.unitName)) },
            confirmButton = {
                TextButton(onClick = onDeleteConfirm) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteCancel) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

@Composable
private fun ActiveRentalActionsContent(
    item: com.henrisusanto.rentipro.feature.home.HomeRentalItem,
    isProcessing: Boolean,
    onReturn: () -> Unit,
    onExtend: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
    ) {
        Text(
            text = item.unitName,
            style = MaterialTheme.typography.titleLarge,
        )
        RentalTimerText(
            snapshot = item.timerSnapshot,
            highlightDueSoon = item.category == HomeRentalCategory.DUE_SOON,
        )
        Text(
            text = item.price.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onReturn,
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.home_returned))
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onExtend,
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.rental_action_extend))
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = if (item.isPaused) onResume else onPause,
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                stringResource(
                    if (item.isPaused) R.string.rental_action_resume else R.string.rental_action_pause
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onDelete,
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.action_delete))
        }
    }
}

@Composable
private fun ExtendPresetSelectionContent(
    presets: List<RentalPresetEntity>,
    isProcessing: Boolean,
    onBack: () -> Unit,
    onPresetSelected: (Long) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconButton(
            onClick = onBack,
            enabled = !isProcessing,
            modifier = Modifier.padding(start = 8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
            )
        }
        Text(
            text = stringResource(R.string.rental_extend_select_preset),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            items(presets, key = { it.id }) { preset ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(
                                R.string.rental_preset_format,
                                preset.durationMinutes,
                                preset.price,
                            ),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isProcessing) { 
                            onPresetSelected(preset.id) 
                        },
                )
                HorizontalDivider()
            }
        }
    }
}
