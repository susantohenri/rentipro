package com.henrisusanto.rentipro.core.data

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.henrisusanto.rentipro.core.locale.LocaleManager
import com.henrisusanto.rentipro.core.model.AppLanguage
import com.henrisusanto.rentipro.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings",
)

class SettingsRepository(private val context: Context) {

    private val dataStore = context.settingsDataStore

    val language: Flow<AppLanguage> = dataStore.data.map { prefs ->
        AppLanguage.fromTag(prefs[KEY_LANGUAGE])
    }

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.fromStorageKey(prefs[KEY_THEME_MODE])
    }

    val dueSoonMinutes: Flow<Int> = dataStore.data.map { prefs ->
        prefs[KEY_DUE_SOON_MINUTES] ?: DEFAULT_DUE_SOON_MINUTES
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_COMPLETED] ?: false
    }

    suspend fun ensureDefaultsInitialized() {
        dataStore.edit { prefs ->
            if (prefs[KEY_LANGUAGE] == null) {
                prefs[KEY_LANGUAGE] = LocaleManager.detectSystemLanguage().tag
                prefs[KEY_LANGUAGE_MANUAL] = false
            }
            if (prefs[KEY_THEME_MODE] == null) {
                prefs[KEY_THEME_MODE] = detectSystemThemeMode().storageKey
            }
            if (prefs[KEY_DUE_SOON_MINUTES] == null) {
                prefs[KEY_DUE_SOON_MINUTES] = DEFAULT_DUE_SOON_MINUTES
            }
        }
    }

    fun ensureDefaultsInitializedBlocking() {
        runBlocking { ensureDefaultsInitialized() }
    }

    fun getLanguageBlocking(): AppLanguage = runBlocking {
        ensureDefaultsInitialized()
        AppLanguage.fromTag(dataStore.data.first()[KEY_LANGUAGE])
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = language.tag
            prefs[KEY_LANGUAGE_MANUAL] = true
        }
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = themeMode.storageKey
        }
    }

    suspend fun setDueSoonMinutes(minutes: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_DUE_SOON_MINUTES] = minutes
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    fun isOnboardingCompletedBlocking(): Boolean = runBlocking {
        ensureDefaultsInitialized()
        dataStore.data.first()[KEY_ONBOARDING_COMPLETED] ?: false
    }

    private fun detectSystemThemeMode(): ThemeMode {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            ThemeMode.DARK
        } else {
            ThemeMode.LIGHT
        }
    }

    companion object {
        const val DEFAULT_DUE_SOON_MINUTES = 5

        private val KEY_LANGUAGE = stringPreferencesKey("language")
        private val KEY_LANGUAGE_MANUAL = booleanPreferencesKey("language_manual")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DUE_SOON_MINUTES = intPreferencesKey("due_soon_minutes")
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
