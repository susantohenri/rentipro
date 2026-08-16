package com.henrisusanto.rentipro.core.ads

import android.content.Context
import kotlinx.serialization.Serializable

@Serializable
data class AdsRemoteConfig(
    val appOpenAdUnitId: String = "",
    val bannerAdUnitId: String = "",
    val interstitialAdUnitId: String = "",
    val rewardedAdUnitId: String = "",
    val nativeAdUnitId: String = "",
    val isAdsEnabled: Boolean = false,
)

/**
 * Fetches remote AdMob configuration.
 * Full implementation in Step 15.
 */
class AdsConfigRepository(private val context: Context) {

  companion object {
    const val REMOTE_CONFIG_URL =
        "https://raw.githubusercontent.com/susantohenri/admob-remote-configs/main/rentipro/ads_config.json"

    val DEFAULT_CONFIG = AdsRemoteConfig(isAdsEnabled = false)
  }

  suspend fun fetchRemoteConfig(): AdsRemoteConfig = DEFAULT_CONFIG
}
