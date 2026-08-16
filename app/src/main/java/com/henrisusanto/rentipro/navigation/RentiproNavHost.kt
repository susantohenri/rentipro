package com.henrisusanto.rentipro.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.henrisusanto.rentipro.R
import com.henrisusanto.rentipro.core.di.AppContainer
import com.henrisusanto.rentipro.core.di.viewModelFactory
import com.henrisusanto.rentipro.feature.history.HistoryScreen
import com.henrisusanto.rentipro.feature.home.HomeScreen
import com.henrisusanto.rentipro.feature.home.HomeViewModel
import com.henrisusanto.rentipro.feature.items.ItemsScreen
import com.henrisusanto.rentipro.feature.items.ItemsViewModel
import com.henrisusanto.rentipro.feature.onboarding.OnboardingScreen
import com.henrisusanto.rentipro.feature.onboarding.OnboardingViewModel
import com.henrisusanto.rentipro.feature.settings.SettingsScreen
import com.henrisusanto.rentipro.feature.settings.SettingsViewModel

@Composable
fun RentiproNavHost(container: AppContainer) {
    val onboardingCompleted by container.settingsRepository.onboardingCompleted
        .collectAsStateWithLifecycle(initialValue = null)

    when (onboardingCompleted) {
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        false -> {
            val viewModel: OnboardingViewModel = viewModel(
                factory = viewModelFactory(container) {
                    OnboardingViewModel(
                        settingsRepository = it.settingsRepository,
                        unitRepository = it.unitRepository,
                        presetRepository = it.presetRepository,
                    )
                },
            )
            OnboardingScreen(viewModel = viewModel)
        }
        true -> MainAppNavHost(container = container)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in BottomNavItem.entries.map { it.route }

    Scaffold(
        topBar = {
            if (showBottomBar) {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate(Routes.SETTINGS) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings_content_description),
                            )
                        }
                    },
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.entries.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.HOME) {
                val viewModel: HomeViewModel = viewModel(
                    factory = viewModelFactory(container) {
                        HomeViewModel(
                            rentalRepository = it.rentalRepository,
                            unitRepository = it.unitRepository,
                            presetRepository = it.presetRepository,
                            settingsRepository = it.settingsRepository,
                        )
                    },
                )
                HomeScreen(viewModel = viewModel)
            }
            composable(Routes.ITEMS) {
                val viewModel: ItemsViewModel = viewModel(
                    factory = viewModelFactory(container) {
                        ItemsViewModel(
                            rentalRepository = it.rentalRepository,
                            unitRepository = it.unitRepository,
                            settingsRepository = it.settingsRepository,
                        )
                    },
                )
                ItemsScreen(viewModel = viewModel)
            }
            composable(Routes.HISTORY) { HistoryScreen() }
            composable(Routes.SETTINGS) {
                val viewModel: SettingsViewModel = viewModel(
                    factory = viewModelFactory(container) { SettingsViewModel(it.settingsRepository) },
                )
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }
    }
}
