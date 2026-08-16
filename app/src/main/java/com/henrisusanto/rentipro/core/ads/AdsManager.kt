package com.henrisusanto.rentipro.core.ads

import android.content.Context

/**
 * Central AdMob manager.
 * Full implementation in Step 15.
 */
class AdsManager(
    private val context: Context,
    private val adsConfigRepository: AdsConfigRepository,
    private val umpConsentManager: UmpConsentManager,
) {

    fun initialize() {
        // MobileAds.initialize() — Step 15
    }
}
