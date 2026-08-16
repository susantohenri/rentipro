package com.henrisusanto.rentipro.core.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.henrisusanto.rentipro.core.model.AppLanguage
import java.util.Locale

object LocaleManager {

    fun detectSystemLanguage(): AppLanguage {
        val locale = LocaleListCompat.getAdjustedDefault()[0] ?: Locale.getDefault()
        return when (locale.language.lowercase(Locale.ROOT)) {
            "in", "id" -> AppLanguage.INDONESIAN
            else -> AppLanguage.ENGLISH
        }
    }

    fun applyAppLanguage(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
    }
}
