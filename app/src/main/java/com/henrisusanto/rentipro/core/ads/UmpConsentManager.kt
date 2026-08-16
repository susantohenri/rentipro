package com.henrisusanto.rentipro.core.ads

import android.content.Context

/**
 * Google UMP consent manager.
 * Full implementation in Step 16.
 */
class UmpConsentManager(private val context: Context) {

    fun canRequestAds(): Boolean = false
}
