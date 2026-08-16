package com.henrisusanto.rentipro.feature.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrisusanto.rentipro.core.data.RentalRepository
import com.henrisusanto.rentipro.core.data.SettingsRepository
import com.henrisusanto.rentipro.core.data.UnitRepository
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity
import com.henrisusanto.rentipro.core.database.model.ActiveRentalWithUnit
import com.henrisusanto.rentipro.core.model.UnitStatus
import com.henrisusanto.rentipro.core.timer.RentalTimer
import com.henrisusanto.rentipro.core.timer.RentalTimerPhase
import com.henrisusanto.rentipro.core.timer.TimerTicker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val rentalRepository: RentalRepository,
    private val unitRepository: UnitRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val nowMillis = MutableStateFlow(System.currentTimeMillis())

    val uiState: StateFlow<ItemsUiState> = combine(
        unitRepository.observeAllUnits(),
        rentalRepository.observeActiveRentalsWithUnits(),
        settingsRepository.dueSoonMinutes,
        nowMillis,
    ) { units, activeRentals, dueSoonMinutes, now ->
        ItemsUiState(items = buildItemRows(units, activeRentals, dueSoonMinutes, now))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ItemsUiState(),
    )

    init {
        viewModelScope.launch {
            TimerTicker.currentTimeMillis().collect { nowMillis.value = it }
        }
        viewModelScope.launch {
            rentalRepository.observeActiveRentalsWithUnits().collect { rentals ->
                val now = System.currentTimeMillis()
                rentals.forEach { item ->
                    rentalRepository.syncUnitStatusFromRental(item.rental, now)
                }
            }
        }
    }

    private fun buildItemRows(
        units: List<RentalUnitEntity>,
        activeRentals: List<ActiveRentalWithUnit>,
        dueSoonMinutes: Int,
        nowMillis: Long,
    ): List<ItemRow> {
        val rentalByUnitId = activeRentals.associateBy { it.rental.unitId }

        return units.map { unit ->
            val active = rentalByUnitId[unit.id]
            val snapshot = active?.rental?.let { rental ->
                RentalTimer.snapshot(rental, nowMillis, dueSoonMinutes)
            }
            ItemRow(
                unitId = unit.id,
                name = unit.name,
                status = unit.status,
                timerSnapshot = snapshot,
                rentalId = active?.rental?.id,
            )
        }.sortedWith(itemPriorityComparator())
    }

    private fun itemPriorityComparator(): Comparator<ItemRow> =
        compareBy<ItemRow> { row ->
            when (row.timerSnapshot?.phase) {
                RentalTimerPhase.OVERDUE -> 0
                RentalTimerPhase.REMAINING ->
                    if (row.timerSnapshot.isDueSoon) 1 else 2
                RentalTimerPhase.PAUSED -> 2
                null -> 3
            }
        }.thenBy { it.name }
}
