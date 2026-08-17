package com.henrisusanto.rentipro.core.ads

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
 * Fetches remote AdMob configuration directly from the GitHub-hosted JSON.
 * No caching — every call fetches fresh data.
 * Falls back to isAdsEnabled=false if anything fails.
 */
class AdsConfigRepository(private val context: Context) {

    companion object {
        const val REMOTE_CONFIG_URL =
            "https://raw.githubusercontent.com/susantohenri/admob-remote-configs/main/rentipro/ads_config.json"

        val DEFAULT_CONFIG = AdsRemoteConfig(isAdsEnabled = false)

        private val json = Json { ignoreUnknownKeys = true }
    }

    suspend fun fetchRemoteConfig(): AdsRemoteConfig = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(REMOTE_CONFIG_URL)
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.useCaches = false
            connection.setRequestProperty("Cache-Control", "no-cache")

            try {
                if (connection.responseCode != 200) {
                    return@withContext DEFAULT_CONFIG
                }
                val body = connection.inputStream.bufferedReader().readText()
                json.decodeFromString<AdsRemoteConfig>(body)
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            // Fallback: ads disabled when remote config fails
            DEFAULT_CONFIG
        }
    }
}
