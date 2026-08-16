package com.henrisusanto.rentipro.core.model

import androidx.annotation.StringRes
import com.henrisusanto.rentipro.R

enum class ThemeMode(
    val storageKey: String,
    @StringRes val labelRes: Int,
) {
    LIGHT("light", R.string.settings_theme_light),
    DARK("dark", R.string.settings_theme_dark),
    ;

    val isDark: Boolean get() = this == DARK

    companion object {
        fun fromStorageKey(key: String?): ThemeMode =
            entries.find { it.storageKey == key } ?: LIGHT
    }
}
