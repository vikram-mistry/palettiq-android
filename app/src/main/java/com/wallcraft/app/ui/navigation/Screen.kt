package com.wallcraft.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Discover : Screen(
        route = "discover",
        title = "Discover",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    )
    
    object Favourites : Screen(
        route = "favourites",
        title = "Favourites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )
    
    object Dark : Screen(
        route = "dark",
        title = "Dark",
        selectedIcon = Icons.Filled.DarkMode,
        unselectedIcon = Icons.Outlined.DarkMode
    )
    
    object Settings : Screen(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
    
    object WallpaperDetail : Screen(
        route = "wallpaper/{wallpaperId}",
        title = "Wallpaper",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    ) {
        fun createRoute(wallpaperId: String) = "wallpaper/$wallpaperId"
    }
    
    companion object {
        val bottomNavItems = listOf(Discover, Favourites, Dark, Settings)
    }
}
