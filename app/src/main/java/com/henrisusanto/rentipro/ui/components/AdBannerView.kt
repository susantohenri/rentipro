package com.henrisusanto.rentipro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Composable for displaying AdMob banner ads.
 * Shows a standard banner ad (320x50) at the bottom of screens.
 * Gracefully handles missing ad unit IDs.
 */
@Composable
fun AdBannerView(
    adUnitId: String,
    modifier: Modifier = Modifier,
) {
    if (adUnitId.isEmpty()) {
        // No ad unit ID configured, skip ad display
        return
    }

    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.surface),
    )
}
