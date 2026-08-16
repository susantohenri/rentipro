package com.henrisusanto.rentipro.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.feature.rental.StartRentalBottomSheet
import com.henrisusanto.rentipro.ui.components.AvailableUnitsRow
import com.henrisusanto.rentipro.ui.components.HomeRentalCard
import com.henrisusanto.rentipro.ui.components.HomeSectionHeader
import com.henrisusanto.rentipro.ui.components.HomeSummaryCard
import com.henrisusanto.rentipro.ui.components.StartRentalButton

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRentalClick: (Long) -> Unit = {},
    onReturnedClick: (Long) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val startRentalState by viewModel.startRentalState.collectAsStateWithLifecycle()

    if (startRentalState.isVisible) {
        StartRentalBottomSheet(
            state = startRentalState,
            onDismiss = viewModel::dismissStartRental,
            onUnitSelected = viewModel::selectUnitForRental,
            onPresetSelected = viewModel::startRentalWithPreset,
            onBackToUnits = viewModel::backToUnitSelection,
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            HomeSummaryCard(
                todayRentalCount = uiState.todayRentalCount,
                todayRevenue = uiState.todayRevenue,
            )
        }

        if (!uiState.hasActiveRentals) {
            item {
                Text(
                    text = stringResource(R.string.home_no_active_rentals),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        }

        if (uiState.overdue.isNotEmpty()) {
            item { HomeSectionHeader(stringResource(R.string.home_section_overdue)) }
            items(uiState.overdue, key = { it.rentalId }) { item ->
                HomeRentalCard(
                    item = item,
                    onClick = { onRentalClick(item.rentalId) },
                    onReturnedClick = { onReturnedClick(item.rentalId) },
                )
            }
        }

        if (uiState.dueSoon.isNotEmpty()) {
            item { HomeSectionHeader(stringResource(R.string.home_section_due_soon)) }
            items(uiState.dueSoon, key = { it.rentalId }) { item ->
                HomeRentalCard(
                    item = item,
                    onClick = { onRentalClick(item.rentalId) },
                )
            }
        }

        if (uiState.rented.isNotEmpty()) {
            item { HomeSectionHeader(stringResource(R.string.home_section_rented)) }
            items(uiState.rented, key = { it.rentalId }) { item ->
                HomeRentalCard(
                    item = item,
                    onClick = { onRentalClick(item.rentalId) },
                )
            }
        }

        item { HomeSectionHeader(stringResource(R.string.home_section_available)) }
        item {
            if (uiState.availableUnits.isEmpty()) {
                Text(
                    text = stringResource(R.string.home_all_units_rented),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                AvailableUnitsRow(
                    unitNames = uiState.availableUnits.map { it.name },
                )
            }
        }

        item {
            StartRentalButton(
                onClick = { viewModel.openStartRental() },
                enabled = uiState.availableUnits.isNotEmpty(),
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )
        }
    }
}
