package com.henrisusanto.rentipro.feature.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    if (uiState.items.isEmpty()) {
        Text(
            text = stringResource(R.string.items_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(uiState.items, key = { it.unitId }) { item ->
            ItemRowCard(item = item)
        }
    }
}

@Composable
private fun ItemRowCard(item: ItemRow) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
    }
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
