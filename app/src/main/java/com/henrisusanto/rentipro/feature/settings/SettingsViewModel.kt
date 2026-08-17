package com.henrisusanto.rentipro.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henrisusanto.rentipro.core.data.PresetRepository
import com.henrisusanto.rentipro.core.data.RentalRepository
import com.henrisusanto.rentipro.core.data.SettingsRepository
import com.henrisusanto.rentipro.core.database.entity.RentalPresetEntity
import com.henrisusanto.rentipro.core.locale.LocaleManager
import com.henrisusanto.rentipro.core.model.AppLanguage
import com.henrisusanto.rentipro.core.model.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val rentalRepository: RentalRepository,
    private val presetRepository: PresetRepository,
) : ViewModel() {

    val language: StateFlow<AppLanguage> = settingsRepository.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppLanguage.ENGLISH)

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.LIGHT)

    val dueSoonMinutes: StateFlow<Int> = settingsRepository.dueSoonMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsRepository.DEFAULT_DUE_SOON_MINUTES)

    val presets: StateFlow<List<RentalPresetEntity>> = presetRepository.observePresets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            LocaleManager.applyAppLanguage(language)
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }

    fun setDueSoonMinutes(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.setDueSoonMinutes(minutes)
            rentalRepository.rescheduleAllAlarms()
        }
    }

    fun addPreset(durationMinutes: Int, price: Int) {
        viewModelScope.launch {
            presetRepository.createPreset(durationMinutes, price)
        }
    }

    fun deletePreset(preset: RentalPresetEntity) {
        viewModelScope.launch {
            presetRepository.deletePreset(preset)
        }
    }
}
