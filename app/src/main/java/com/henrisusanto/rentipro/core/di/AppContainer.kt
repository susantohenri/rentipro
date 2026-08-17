package com.henrisusanto.rentipro.core.di

import android.content.Context
import com.henrisusanto.rentipro.core.ads.AdsConfigRepository
import com.henrisusanto.rentipro.core.ads.AdsManager
import com.henrisusanto.rentipro.core.ads.UmpConsentManager
import com.henrisusanto.rentipro.core.alarm.AlarmScheduler
import com.henrisusanto.rentipro.core.alarm.AlarmSchedulerImpl
import com.henrisusanto.rentipro.core.data.PresetRepository
import com.henrisusanto.rentipro.core.data.RentalRepository
import com.henrisusanto.rentipro.core.data.SettingsRepository
import com.henrisusanto.rentipro.core.data.UnitRepository
import com.henrisusanto.rentipro.core.database.RentiproDatabase
import com.henrisusanto.rentipro.core.notification.NotificationHelper

/**
 * Manual dependency injection container.
 * Repositories and managers are lazily initialized here.
 * Add new dependencies as features are implemented.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    // --- Ads (Step 15–16) ---
    val adsConfigRepository: AdsConfigRepository by lazy {
        AdsConfigRepository(appContext)
    }

    val umpConsentManager: UmpConsentManager by lazy {
        UmpConsentManager(appContext)
    }

    val adsManager: AdsManager by lazy {
        AdsManager(appContext, adsConfigRepository, umpConsentManager)
    }

    // --- Settings (Step 2) ---
    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(appContext)
    }

    // --- Database & Repositories (Step 3) ---
    val database: RentiproDatabase by lazy {
        RentiproDatabase.create(appContext)
    }

    val unitRepository: UnitRepository by lazy {
        UnitRepository(database.rentalUnitDao())
    }

    val presetRepository: PresetRepository by lazy {
        PresetRepository(database.rentalPresetDao())
    }

    val rentalRepository: RentalRepository by lazy {
        RentalRepository(
            rentalDao = database.rentalDao(),
            rentalExtensionDao = database.rentalExtensionDao(),
            rentalUnitDao = database.rentalUnitDao(),
            settingsRepository = settingsRepository,
            alarmScheduler = alarmScheduler,
        )
    }

    // --- Alarm & Notification (Step 8) ---
    val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(appContext)
    }

    val alarmScheduler: AlarmScheduler by lazy {
        AlarmSchedulerImpl(appContext)
    }
}
