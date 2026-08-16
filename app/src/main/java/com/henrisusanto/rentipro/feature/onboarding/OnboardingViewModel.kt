package com.henrisusanto.rentipro.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrisusanto.rentipro.core.data.PresetRepository
import com.henrisusanto.rentipro.core.data.SettingsRepository
import com.henrisusanto.rentipro.core.data.UnitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository,
    private val unitRepository: UnitRepository,
    private val presetRepository: PresetRepository,
) : ViewModel() {

    private val _step = MutableStateFlow(OnboardingStep.WELCOME)
    val step: StateFlow<OnboardingStep> = _step.asStateFlow()

    private val _unitNames = MutableStateFlow<List<String>>(emptyList())
    val unitNames: StateFlow<List<String>> = _unitNames.asStateFlow()

    private val _presets = MutableStateFlow<List<OnboardingPresetDraft>>(emptyList())
    val presets: StateFlow<List<OnboardingPresetDraft>> = _presets.asStateFlow()

    private val _error = MutableStateFlow<OnboardingError?>(null)
    val error: StateFlow<OnboardingError?> = _error.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        viewModelScope.launch {
            resumeIfNeeded()
        }
    }

    fun onGetStarted() {
        if (_unitNames.value.isEmpty()) {
            _unitNames.value = DEFAULT_UNIT_NAMES
        }
        _error.value = null
        _step.value = OnboardingStep.UNITS
    }

    fun updateUnitName(index: Int, name: String) {
        _unitNames.update { names ->
            names.toMutableList().also { it[index] = name }
        }
    }

    fun updatePresetDuration(id: Long, duration: String) {
        _presets.update { list ->
            list.map { preset ->
                if (preset.id == id) preset.copy(durationMinutes = duration) else preset
            }
        }
    }

    fun updatePresetPrice(id: Long, price: String) {
        _presets.update { list ->
            list.map { preset ->
                if (preset.id == id) preset.copy(price = price) else preset
            }
        }
    }

    fun addPreset() {
        _presets.update { list ->
            list + OnboardingPresetDraft(durationMinutes = "30", price = "10")
        }
    }

    fun removePreset(id: Long) {
        _presets.update { list -> list.filterNot { it.id == id } }
    }

    fun goBack() {
        _error.value = null
        when (_step.value) {
            OnboardingStep.UNITS -> _step.value = OnboardingStep.WELCOME
            OnboardingStep.PRESETS -> {
                viewModelScope.launch {
                    if (_unitNames.value.isEmpty() && unitRepository.hasAnyUnits()) {
                        _unitNames.value = unitRepository.getAllUnits().map { it.name }
                    }
                    _step.value = OnboardingStep.UNITS
                }
            }
            OnboardingStep.WELCOME -> Unit
        }
    }

    fun saveUnitsAndContinue() {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            val trimmed = _unitNames.value.map { it.trim() }
            if (trimmed.any { it.isEmpty() }) {
                _error.value = OnboardingError.UNIT_NAME_EMPTY
                _isSaving.value = false
                return@launch
            }
            if (!unitRepository.hasAnyUnits()) {
                unitRepository.createUnits(trimmed)
            }
            if (_presets.value.isEmpty()) {
                _presets.value = defaultPresetDrafts()
            }
            _step.value = OnboardingStep.PRESETS
            _isSaving.value = false
        }
    }

    fun finishOnboarding(onCompleted: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            val parsedPresets = parsePresets()
            if (parsedPresets == null) {
                _isSaving.value = false
                return@launch
            }
            if (!presetRepository.hasAnyPresets()) {
                presetRepository.createPresets(parsedPresets)
            }
            settingsRepository.setOnboardingCompleted(true)
            _isSaving.value = false
            onCompleted()
        }
    }

    private suspend fun resumeIfNeeded() {
        val hasUnits = unitRepository.hasAnyUnits()
        val hasPresets = presetRepository.hasAnyPresets()
        when {
            hasUnits && hasPresets -> {
                settingsRepository.setOnboardingCompleted(true)
            }
            hasUnits -> {
                _unitNames.value = unitRepository.getAllUnits().map { it.name }
                _step.value = OnboardingStep.PRESETS
                if (_presets.value.isEmpty()) {
                    _presets.value = defaultPresetDrafts()
                }
            }
        }
    }

    private fun parsePresets(): List<Pair<Int, Int>>? {
        val drafts = _presets.value
        if (drafts.isEmpty()) {
            _error.value = OnboardingError.PRESET_REQUIRED
            return null
        }
        val parsed = mutableListOf<Pair<Int, Int>>()
        for (draft in drafts) {
            val duration = draft.durationMinutes.trim().toIntOrNull()
            if (duration == null || duration <= 0) {
                _error.value = OnboardingError.INVALID_DURATION
                return null
            }
            val price = draft.price.trim().toIntOrNull()
            if (price == null || price < 0) {
                _error.value = OnboardingError.INVALID_PRICE
                return null
            }
            parsed += duration to price
        }
        return parsed
    }

    private fun defaultPresetDrafts(): List<OnboardingPresetDraft> =
        DEFAULT_PRESETS.map { (duration, price) ->
            OnboardingPresetDraft(
                durationMinutes = duration.toString(),
                price = price.toString(),
            )
        }

    companion object {
        val DEFAULT_UNIT_NAMES = listOf("#01", "#02", "#03", "#04", "#05")
        val DEFAULT_PRESETS = listOf(15 to 5, 30 to 10, 60 to 15)
    }
}
