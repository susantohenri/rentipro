package com.henrisusanto.rentipro.core.ads

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Google UMP consent manager.
 * Handles requesting and displaying the consent form.
 */
class UmpConsentManager(private val context: Context) {

    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    /** Helper variable to determine if the app can request ads. */
    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    /**
     * Requests consent information and displays the consent form if required.
     */
    fun gatherConsent(
        activity: Activity,
        onConsentGatheringCompleteListener: (ConsentError?) -> Unit
    ) {
        // For testing purposes, you can force a geography (e.g., EEA)
        // val debugSettings = ConsentDebugSettings.Builder(context)
        //     .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
        //     .addTestDeviceHashedId("TEST-DEVICE-HASHED-ID")
        //     .build()

        val params = ConsentRequestParameters.Builder()
            // .setConsentDebugSettings(debugSettings)
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    // Consent has been gathered.
                    onConsentGatheringCompleteListener(
                        if (formError != null) ConsentError(formError.errorCode, formError.message) else null
                    )
                }
            },
            { requestConsentError ->
                onConsentGatheringCompleteListener(
                    ConsentError(requestConsentError.errorCode, requestConsentError.message)
                )
            }
        )
    }

    /**
     * Resets consent state for testing purposes.
     */
    fun reset() {
        consentInformation.reset()
    }
}

data class ConsentError(val code: Int, val message: String)
