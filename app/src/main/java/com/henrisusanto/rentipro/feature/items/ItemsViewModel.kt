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
    private val _dialogState = MutableStateFlow(ItemsUiState())

    val uiState: StateFlow<ItemsUiState> = combine(
        unitRepository.observeAllUnits(),
        rentalRepository.observeActiveRentalsWithUnits(),
        settingsRepository.dueSoonMinutes,
        nowMillis,
        _dialogState,
    ) { units, activeRentals, dueSoonMinutes, now, dialog ->
        val items = buildItemRows(units, activeRentals, dueSoonMinutes, now)
        dialog.copy(items = items)
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

    fun openAddDialog() {
        _dialogState.value = _dialogState.value.copy(showAddDialog = true)
    }

    fun dismissAddDialog() {
        _dialogState.value = _dialogState.value.copy(
            showAddDialog = false,
            addText = "",
        )
    }

    fun updateAddText(text: String) {
        _dialogState.value = _dialogState.value.copy(addText = text)
    }

    fun confirmAdd() {
        if (_dialogState.value.isProcessing) return
        val newName = _dialogState.value.addText.trim()
        if (newName.isEmpty()) return

        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(isProcessing = true)
            unitRepository.createUnit(newName)
            _dialogState.value = _dialogState.value.copy(
                showAddDialog = false,
                addText = "",
                isProcessing = false,
            )
        }
    }


    fun openRenameDialog(unitId: Long) {
        val item = uiState.value.items.find { it.unitId == unitId } ?: return
        _dialogState.value = _dialogState.value.copy(
            showRenameDialog = true,
            renameUnitId = unitId,
            renameText = item.name,
        )
    }

    fun dismissRenameDialog() {
        _dialogState.value = _dialogState.value.copy(
            showRenameDialog = false,
            renameUnitId = null,
            renameText = "",
        )
    }

    fun updateRenameText(text: String) {
        _dialogState.value = _dialogState.value.copy(renameText = text)
    }

    fun confirmRename() {
        if (_dialogState.value.isProcessing) return
        val unitId = _dialogState.value.renameUnitId ?: return
        val newName = _dialogState.value.renameText.trim()
        if (newName.isEmpty()) return

        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(isProcessing = true)
            unitRepository.renameUnit(unitId, newName)
            _dialogState.value = _dialogState.value.copy(
                showRenameDialog = false,
                renameUnitId = null,
                renameText = "",
                isProcessing = false,
            )
        }
    }

    fun openDeleteConfirm(unitId: Long) {
        val item = uiState.value.items.find { it.unitId == unitId } ?: return
        _dialogState.value = _dialogState.value.copy(
            showDeleteConfirmation = true,
            deleteUnitId = unitId,
            deleteUnitName = item.name,
        )
    }

    fun cancelDeleteConfirm() {
        _dialogState.value = _dialogState.value.copy(
            showDeleteConfirmation = false,
            deleteUnitId = null,
            deleteUnitName = "",
        )
    }

    fun dismissDeleteError() {
        _dialogState.value = _dialogState.value.copy(deleteError = null)
    }

    fun confirmDelete() {
        if (_dialogState.value.isProcessing) return
        val unitId = _dialogState.value.deleteUnitId ?: return

        val item = uiState.value.items.find { it.unitId == unitId }
        if (item?.status == UnitStatus.RENTED || item?.status == UnitStatus.OVERDUE) {
            _dialogState.value = _dialogState.value.copy(
                showDeleteConfirmation = false,
                deleteUnitId = null,
                deleteUnitName = "",
                deleteError = "Cannot delete rented unit",
            )
            return
        }

        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(isProcessing = true)
            rentalRepository.deleteAllRentalsByUnitId(unitId)
            unitRepository.deleteUnit(unitId)
            _dialogState.value = _dialogState.value.copy(
                showDeleteConfirmation = false,
                deleteUnitId = null,
                deleteUnitName = "",
                isProcessing = false,
            )
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
        }.thenBy { it.unitId }
}
