package com.henrisusanto.rentipro.core.model

import androidx.annotation.StringRes
import com.henrisusanto.rentipro.R

enum class AppLanguage(
    val tag: String,
    @StringRes val labelRes: Int,
) {
    ENGLISH("en", R.string.settings_language_english),
    INDONESIAN("in", R.string.settings_language_indonesian),
    ;

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            entries.find { it.tag == tag } ?: ENGLISH
    }
}
