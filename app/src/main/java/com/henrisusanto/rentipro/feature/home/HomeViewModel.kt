package com.henrisusanto.rentipro.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrisusanto.rentipro.core.ads.AdsConfigRepository
import com.henrisusanto.rentipro.core.data.PresetRepository
import com.henrisusanto.rentipro.core.data.RentalRepository
import com.henrisusanto.rentipro.core.data.SettingsRepository
import com.henrisusanto.rentipro.core.data.UnitRepository
import com.henrisusanto.rentipro.core.database.entity.RentalUnitEntity
import com.henrisusanto.rentipro.core.timer.TimerTicker
import com.henrisusanto.rentipro.feature.rental.ActiveRentalSheetUiState
import com.henrisusanto.rentipro.feature.rental.StartRentalError
import com.henrisusanto.rentipro.feature.rental.StartRentalStep
import com.henrisusanto.rentipro.feature.rental.StartRentalUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val rentalRepository: RentalRepository,
    private val unitRepository: UnitRepository,
    private val presetRepository: PresetRepository,
    private val settingsRepository: SettingsRepository,
    private val adsConfigRepository: AdsConfigRepository,
) : ViewModel() {

    private val nowMillis = MutableStateFlow(System.currentTimeMillis())
    private val bannerAdUnitId = MutableStateFlow("")

    private val _startRentalState = MutableStateFlow(StartRentalUiState())
    val startRentalState: StateFlow<StartRentalUiState> = _startRentalState.asStateFlow()

    private val _rentalSheetState = MutableStateFlow(ActiveRentalSheetUiState())
    val rentalSheetState: StateFlow<ActiveRentalSheetUiState> = _rentalSheetState.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        combine(
            rentalRepository.observeActiveRentalsWithUnits(),
            unitRepository.observeAllUnits(),
            settingsRepository.dueSoonMinutes,
        ) { activeRentals, units, dueSoonMinutes ->
            Triple(activeRentals, units, dueSoonMinutes)
        },
        combine(
            rentalRepository.observeTodayCompletedCount(),
            rentalRepository.observeTodayRevenue(),
            nowMillis,
            bannerAdUnitId,
        ) { todayCount, todayRevenue, now, adUnitId ->
            Pair(Triple(todayCount, todayRevenue, now), adUnitId)
        },
    ) { rentalData, statsWithAd ->
        buildUiState(
            activeRentals = rentalData.first,
            units = rentalData.second,
            dueSoonMinutes = rentalData.third,
            todayCount = statsWithAd.first.first,
            todayRevenue = statsWithAd.first.second,
            nowMillis = statsWithAd.first.third,
            bannerAdUnitId = statsWithAd.second,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
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
        // Fetch ads configuration (Step 15)
        viewModelScope.launch {
            try {
                val adsConfig = adsConfigRepository.fetchRemoteConfig()
                if (adsConfig.isAdsEnabled) {
                    bannerAdUnitId.value = adsConfig.bannerAdUnitId
                }
            } catch (e: Exception) {
                // Silently fail — ads optional
            }
        }
    }

    fun openStartRental() {
        viewModelScope.launch {
            val availableUnits = uiState.value.availableUnits
            if (availableUnits.isEmpty()) {
                _startRentalState.value = StartRentalUiState(
                    isVisible = true,
                    error = StartRentalError.NO_UNITS,
                )
                return@launch
            }
            val presets = presetRepository.getPresets()
            if (presets.isEmpty()) {
                _startRentalState.value = StartRentalUiState(
                    isVisible = true,
                    error = StartRentalError.NO_PRESETS,
                )
                return@launch
            }
            if (availableUnits.size == 1) {
                val unit = availableUnits.first()
                _startRentalState.value = StartRentalUiState(
                    isVisible = true,
                    step = StartRentalStep.SELECT_PRESET,
                    availableUnits = availableUnits,
                    presets = presets,
                    selectedUnitId = unit.id,
                    selectedUnitName = unit.name,
                )
            } else {
                _startRentalState.value = StartRentalUiState(
                    isVisible = true,
                    step = StartRentalStep.SELECT_UNIT,
                    availableUnits = availableUnits,
                    presets = presets,
                )
            }
        }
    }

    fun dismissStartRental() {
        _startRentalState.value = StartRentalUiState()
    }

    fun selectUnitForRental(unitId: Long) {
        val unit = _startRentalState.value.availableUnits.find { it.id == unitId } ?: return
        _startRentalState.value = _startRentalState.value.copy(
            step = StartRentalStep.SELECT_PRESET,
            selectedUnitId = unit.id,
            selectedUnitName = unit.name,
            error = null,
        )
    }

    fun backToUnitSelection() {
        _startRentalState.value = _startRentalState.value.copy(
            step = StartRentalStep.SELECT_UNIT,
            selectedUnitId = null,
            selectedUnitName = null,
        )
    }

    fun openRentalSheet(rentalId: Long) {
        val item = uiState.value.activeRentals.find { it.rentalId == rentalId } ?: return
        _rentalSheetState.value = ActiveRentalSheetUiState(item = item)
    }

    fun dismissRentalSheet() {
        _rentalSheetState.value = ActiveRentalSheetUiState()
    }

    fun returnRental(rentalId: Long) {
        if (_rentalSheetState.value.isProcessing) return
        viewModelScope.launch {
            _rentalSheetState.value = _rentalSheetState.value.copy(isProcessing = true)
            val rental = rentalRepository.getRental(rentalId) ?: return@launch
            rentalRepository.returnRental(rental)
            _rentalSheetState.value = ActiveRentalSheetUiState()
        }
    }

    fun requestDeleteRental() {
        _rentalSheetState.value = _rentalSheetState.value.copy(showDeleteConfirmation = true)
    }

    fun cancelDeleteRental() {
        _rentalSheetState.value = _rentalSheetState.value.copy(showDeleteConfirmation = false)
    }

    fun confirmDeleteRental() {
        if (_rentalSheetState.value.isProcessing) return
        viewModelScope.launch {
            val rentalId = _rentalSheetState.value.item?.rentalId ?: return@launch
            _rentalSheetState.value = _rentalSheetState.value.copy(isProcessing = true)
            val rental = rentalRepository.getRental(rentalId) ?: return@launch
            rentalRepository.deleteActiveRental(rental)
            _rentalSheetState.value = ActiveRentalSheetUiState()
        }
    }

    fun openExtendSelection() {
        viewModelScope.launch {
            val presets = presetRepository.getPresets()
            _rentalSheetState.value = _rentalSheetState.value.copy(
                showExtendSelection = true,
                presets = presets,
            )
        }
    }

    fun cancelExtendSelection() {
        _rentalSheetState.value = _rentalSheetState.value.copy(
            showExtendSelection = false,
            presets = emptyList(),
        )
    }

    fun extendRentalWithPreset(presetId: Long) {
        if (_rentalSheetState.value.isProcessing) return
        val rentalId = _rentalSheetState.value.item?.rentalId ?: return
        val preset = _rentalSheetState.value.presets.find { it.id == presetId } ?: return

        viewModelScope.launch {
            _rentalSheetState.value = _rentalSheetState.value.copy(isProcessing = true)
            val rental = rentalRepository.getRental(rentalId) ?: return@launch
            rentalRepository.extendRental(rental, preset)
            _rentalSheetState.value = ActiveRentalSheetUiState()
        }
    }

    fun pauseRental() {
        if (_rentalSheetState.value.isProcessing) return
        val rentalId = _rentalSheetState.value.item?.rentalId ?: return

        viewModelScope.launch {
            _rentalSheetState.value = _rentalSheetState.value.copy(isProcessing = true)
            val rental = rentalRepository.getRental(rentalId) ?: return@launch
            rentalRepository.pauseRental(rental)
            _rentalSheetState.value = ActiveRentalSheetUiState()
        }
    }

    fun resumeRental() {
        if (_rentalSheetState.value.isProcessing) return
        val rentalId = _rentalSheetState.value.item?.rentalId ?: return

        viewModelScope.launch {
            _rentalSheetState.value = _rentalSheetState.value.copy(isProcessing = true)
            val rental = rentalRepository.getRental(rentalId) ?: return@launch
            rentalRepository.resumeRental(rental)
            _rentalSheetState.value = ActiveRentalSheetUiState()
        }
    }

    fun startRentalWithPreset(presetId: Long) {
        val state = _startRentalState.value
        val unitId = state.selectedUnitId ?: return
        val preset = state.presets.find { it.id == presetId } ?: return

        viewModelScope.launch {
            _startRentalState.value = state.copy(isStarting = true)
            if (rentalRepository.hasActiveRental(unitId)) {
                dismissStartRental()
                return@launch
            }
            rentalRepository.startRental(unitId, preset)
            dismissStartRental()
        }
    }

    private fun buildUiState(
        activeRentals: List<com.henrisusanto.rentipro.core.database.model.ActiveRentalWithUnit>,
        units: List<RentalUnitEntity>,
        dueSoonMinutes: Int,
        todayCount: Int,
        todayRevenue: Int,
        nowMillis: Long,
        bannerAdUnitId: String = "",
    ): HomeUiState {
        val (overdue, dueSoon, rented) = HomeRentalMapper.buildActiveSections(
            activeRentals = activeRentals,
            dueSoonMinutes = dueSoonMinutes,
            nowMillis = nowMillis,
        )

        val activeUnitIds = activeRentals.map { it.rental.unitId }.toSet()
        val availableUnits = units.filter { it.id !in activeUnitIds }

        return HomeUiState(
            overdue = overdue,
            dueSoon = dueSoon,
            rented = rented,
            availableUnits = availableUnits,
            todayRentalCount = todayCount,
            todayRevenue = todayRevenue,
            hasActiveRentals = activeRentals.isNotEmpty(),
            bannerAdUnitId = bannerAdUnitId,
        )
    }
}
