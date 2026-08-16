package com.henrisusanto.rentipro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.feature.home.HomeRentalCategory
import com.henrisusanto.rentipro.feature.home.HomeRentalItem
import com.henrisusanto.rentipro.ui.components.RentalTimerText

@Composable
fun HomeRentalCard(
    item: HomeRentalItem,
    onClick: () -> Unit,
    onReturnedClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.unitName,
                    style = MaterialTheme.typography.titleMedium,
                )
                RentalTimerText(
                    snapshot = item.timerSnapshot,
                    highlightDueSoon = item.category == HomeRentalCategory.DUE_SOON,
                )
                if (item.isPaused) {
                    Text(
                        text = stringResource(R.string.home_paused),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = item.price.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (item.category == HomeRentalCategory.OVERDUE && onReturnedClick != null) {
                Button(onClick = onReturnedClick) {
                    Text(stringResource(R.string.home_returned))
                }
            }
        }
    }
}

@Composable
fun HomeSummaryCard(
    todayRentalCount: Int,
    todayRevenue: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SummaryMetric(
                label = stringResource(R.string.home_summary_rentals_today),
                value = todayRentalCount.toString(),
                modifier = Modifier.weight(1f),
            )
            SummaryMetric(
                label = stringResource(R.string.home_summary_revenue_today),
                value = todayRevenue.toString(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun AvailableUnitsRow(
    unitNames: List<String>,
    modifier: Modifier = Modifier,
) {
    if (unitNames.isEmpty()) return
    Text(
        text = unitNames.joinToString(stringResource(R.string.home_available_separator)),
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier,
    )
}

@Composable
fun StartRentalButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.home_start_rental))
    }
}

@Composable
fun HomeSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 8.dp),
    )
}
