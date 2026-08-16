package com.henrisusanto.rentipro.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector
import com.henrisusanto.rentipro.R

enum class BottomNavItem(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home(Routes.HOME, R.string.nav_home, Icons.Default.Home),
    Items(Routes.ITEMS, R.string.nav_items, Icons.Default.Inventory2),
    History(Routes.HISTORY, R.string.nav_history, Icons.Default.History),
}
