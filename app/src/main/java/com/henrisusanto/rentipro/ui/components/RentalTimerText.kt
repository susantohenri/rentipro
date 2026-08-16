package com.henrisusanto.rentipro.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.core.timer.RentalTimerPhase
import com.henrisusanto.rentipro.core.timer.RentalTimerSnapshot

@Composable
fun RentalTimerText(
    snapshot: RentalTimerSnapshot,
    modifier: Modifier = Modifier,
    highlightDueSoon: Boolean = true,
) {
    val color = when (snapshot.phase) {
        RentalTimerPhase.OVERDUE -> MaterialTheme.colorScheme.error
        RentalTimerPhase.REMAINING ->
            if (highlightDueSoon && snapshot.isDueSoon) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        RentalTimerPhase.PAUSED -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = formatTimerLabel(snapshot),
        style = MaterialTheme.typography.titleLarge,
        color = color,
        modifier = modifier,
    )
}

@Composable
fun RentalTimerText(
    snapshot: RentalTimerSnapshot,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = formatTimerLabel(snapshot),
        style = MaterialTheme.typography.titleLarge,
        color = color,
        modifier = modifier,
    )
}

@Composable
private fun formatTimerLabel(snapshot: RentalTimerSnapshot): String =
    when (snapshot.phase) {
        RentalTimerPhase.OVERDUE ->
            stringResource(R.string.home_overdue, snapshot.formattedTime)
        RentalTimerPhase.REMAINING ->
            stringResource(R.string.home_remaining, snapshot.formattedTime)
        RentalTimerPhase.PAUSED ->
            stringResource(R.string.home_remaining, snapshot.formattedTime)
    }
