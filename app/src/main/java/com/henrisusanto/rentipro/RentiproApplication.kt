package com.henrisusanto.rentipro

import android.app.Application
import com.henrisusanto.rentipro.core.di.AppContainer
import com.henrisusanto.rentipro.core.locale.LocaleManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RentiproApplication : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.settingsRepository.ensureDefaultsInitializedBlocking()
        LocaleManager.applyAppLanguage(container.settingsRepository.getLanguageBlocking())
        container.notificationHelper.createChannel()

        // Re-arm alarms for active rentals (covers app updates and missed reschedules).
        applicationScope.launch { container.rentalRepository.rescheduleAllAlarms() }
    }
}

fun Application.appContainer(): AppContainer =
    (this as RentiproApplication).container
