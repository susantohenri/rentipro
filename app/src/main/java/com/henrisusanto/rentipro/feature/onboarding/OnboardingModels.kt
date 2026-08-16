package com.henrisusanto.rentipro.feature.onboarding

import androidx.annotation.StringRes
import com.henrisusanto.rentipro.R

enum class OnboardingStep {
    WELCOME,
    UNITS,
    PRESETS,
}

enum class OnboardingError(@StringRes val messageRes: Int) {
    UNIT_NAME_EMPTY(R.string.onboarding_error_unit_name_empty),
    INVALID_DURATION(R.string.onboarding_error_invalid_duration),
    INVALID_PRICE(R.string.onboarding_error_invalid_price),
    PRESET_REQUIRED(R.string.onboarding_error_preset_required),
}

data class OnboardingPresetDraft(
    val id: Long = System.nanoTime(),
    val durationMinutes: String,
    val price: String,
)
