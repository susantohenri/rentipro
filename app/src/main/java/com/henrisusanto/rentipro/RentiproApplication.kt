package com.henrisusanto.rentipro

import android.app.Application
import com.henrisusanto.rentipro.core.di.AppContainer
import com.henrisusanto.rentipro.core.locale.LocaleManager

class RentiproApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.settingsRepository.ensureDefaultsInitializedBlocking()
        LocaleManager.applyAppLanguage(container.settingsRepository.getLanguageBlocking())
    }
}

fun Application.appContainer(): AppContainer =
    (this as RentiproApplication).container
