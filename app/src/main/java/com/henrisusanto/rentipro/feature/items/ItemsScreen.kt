package com.henrisusanto.rentipro.feature.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.core.model.UnitStatus
import com.henrisusanto.rentipro.ui.components.RentalTimerText

@Composable
fun ItemsScreen(viewModel: ItemsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openAddDialog) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.items_add_unit))
            }
        }
    ) { paddingValues ->
        if (uiState.items.isEmpty()) {
            Text(
                text = stringResource(R.string.items_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.items, key = { it.unitId }) { item ->
                    ItemRowCard(
                        item = item,
                        onLongPress = { viewModel.openRenameDialog(item.unitId) },
                        onDelete = { viewModel.openDeleteConfirm(item.unitId) },
                    )
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        AddUnitDialog(
            unitName = uiState.addText,
            onNameChange = viewModel::updateAddText,
            onConfirm = viewModel::confirmAdd,
            onDismiss = viewModel::dismissAddDialog,
            isProcessing = uiState.isProcessing,
        )
    }

    if (uiState.showRenameDialog) {
        RenameUnitDialog(
            unitName = uiState.renameText,
            onNameChange = viewModel::updateRenameText,
            onConfirm = viewModel::confirmRename,
            onDismiss = viewModel::dismissRenameDialog,
            isProcessing = uiState.isProcessing,
        )
    }

    if (uiState.showDeleteConfirmation) {
        DeleteUnitConfirmDialog(
            unitName = uiState.deleteUnitName,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::cancelDeleteConfirm,
            isProcessing = uiState.isProcessing,
        )
    }

    if (uiState.deleteError != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteError,
            title = { Text(stringResource(R.string.items_delete_dialog_title)) },
            text = { Text(stringResource(R.string.items_cannot_delete_rented)) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissDeleteError) {
                    Text(stringResource(R.string.action_confirm))
                }
            }
        )
    }
}

@Composable
private fun ItemRowCard(
    item: ItemRow,
    onLongPress: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = true) { onLongPress() },
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = statusLabel(item.status),
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor(item.status),
                )
                if (item.timerSnapshot != null) {
                    RentalTimerText(
                        snapshot = item.timerSnapshot,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    if (item.timerSnapshot.phase == com.henrisusanto.rentipro.core.timer.RentalTimerPhase.PAUSED) {
                        Text(
                            text = stringResource(R.string.home_paused),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
            }
        }
    }
}

@Composable
private fun AddUnitDialog(
    unitName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isProcessing: Boolean,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.items_add_dialog_title)) },
        text = {
            TextField(
                value = unitName,
                onValueChange = onNameChange,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.items_add_dialog_hint)) },
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isProcessing && unitName.trim().isNotEmpty(),
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing,
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Composable
private fun RenameUnitDialog(
    unitName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isProcessing: Boolean,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.items_rename_dialog_title)) },
        text = {
            TextField(
                value = unitName,
                onValueChange = onNameChange,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.items_rename_dialog_hint)) },
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isProcessing && unitName.trim().isNotEmpty(),
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing,
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Composable
private fun DeleteUnitConfirmDialog(
    unitName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isProcessing: Boolean,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.items_delete_dialog_title)) },
        text = { Text(stringResource(R.string.items_delete_dialog_message, unitName)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isProcessing,
            ) {
                Text(stringResource(R.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing,
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Composable
private fun statusLabel(status: UnitStatus): String = when (status) {
    UnitStatus.AVAILABLE -> stringResource(R.string.items_status_available)
    UnitStatus.RENTED -> stringResource(R.string.items_status_rented)
    UnitStatus.OVERDUE -> stringResource(R.string.items_status_overdue)
}

@Composable
private fun statusColor(status: UnitStatus) = when (status) {
    UnitStatus.AVAILABLE -> MaterialTheme.colorScheme.primary
    UnitStatus.RENTED -> MaterialTheme.colorScheme.onSurface
    UnitStatus.OVERDUE -> MaterialTheme.colorScheme.error
}
