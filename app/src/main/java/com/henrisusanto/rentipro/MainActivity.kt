package com.henrisusanto.rentipro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.henrisusanto.rentipro.core.model.ThemeMode
import com.henrisusanto.rentipro.navigation.RentiproNavHost
import com.henrisusanto.rentipro.ui.theme.RentiproTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = application.appContainer()

        setContent {
            val themeMode by container.settingsRepository.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.LIGHT)

            RentiproTheme(darkTheme = themeMode.isDark) {
                RentiproNavHost(container = container)
            }
        }
    }
}
