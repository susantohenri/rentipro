package com.henrisusanto.rentipro.core.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds

/**
 * Central AdMob manager.
 * Initializes MobileAds SDK and manages ad configuration.
 * Implementation in Step 15.
 */
class AdsManager(
    private val context: Context,
    private val adsConfigRepository: AdsConfigRepository,
    private val umpConsentManager: UmpConsentManager,
) {

    private var isMobileAdsInitializeCalled = false

    fun initialize() {
        if (isMobileAdsInitializeCalled) return
        if (umpConsentManager.canRequestAds) {
            isMobileAdsInitializeCalled = true
            // Initialize Google Mobile Ads SDK
            MobileAds.initialize(context)
        }
    }
}
