package com.henrisusanto.rentipro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrisusanto.rentipro.core.model.ThemeMode
import com.henrisusanto.rentipro.navigation.RentiproNavHost
import com.henrisusanto.rentipro.ui.theme.RentiproTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = application.appContainer()

        container.umpConsentManager.gatherConsent(this) { consentError ->
            // if (consentError != null) {
            //     android.util.Log.w("UMP", "Consent error: ${consentError.message}")
            // }
            // Try to initialize ads (will only succeed if consent allows)
            container.adsManager.initialize()
        }

        setContent {
            val themeMode by container.settingsRepository.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.LIGHT)
            val onboardingCompleted by container.settingsRepository.onboardingCompleted
                .collectAsStateWithLifecycle(initialValue = null)

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { /* result handled implicitly; notifications are optional */ }

            // Ask once when the user enters the main app (Android 13+).
            LaunchedEffect(onboardingCompleted) {
                if (onboardingCompleted == true) {
                    requestNotificationPermissionIfNeeded(notificationPermissionLauncher)
                }
            }

            RentiproTheme(darkTheme = themeMode.isDark) {
                RentiproNavHost(container = container)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded(
        launcher: androidx.activity.result.ActivityResultLauncher<String>,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
