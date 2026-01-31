package com.wallcraft.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wallcraft.app.ui.dark.DarkScreen
import com.wallcraft.app.ui.detail.WallpaperDetailScreen
import com.wallcraft.app.ui.discover.DiscoverScreen
import com.wallcraft.app.ui.favourites.FavouritesScreen
import com.wallcraft.app.ui.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Check if we should show bottom navigation
    val showBottomNav = Screen.bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Discover.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Discover.route,
                enterTransition = { fadeIn(tween(300)) },
                exitTransition = { fadeOut(tween(300)) }
            ) {
                DiscoverScreen(
                    onWallpaperClick = { wallpaperId ->
                        navController.navigate(Screen.WallpaperDetail.createRoute(wallpaperId))
                    }
                )
            }
            
            composable(
                route = Screen.Favourites.route,
                enterTransition = { fadeIn(tween(300)) },
                exitTransition = { fadeOut(tween(300)) }
            ) {
                FavouritesScreen(
                    onWallpaperClick = { wallpaperId ->
                        navController.navigate(Screen.WallpaperDetail.createRoute(wallpaperId))
                    }
                )
            }
            
            composable(
                route = Screen.Dark.route,
                enterTransition = { fadeIn(tween(300)) },
                exitTransition = { fadeOut(tween(300)) }
            ) {
                DarkScreen(
                    onWallpaperClick = { wallpaperId ->
                        navController.navigate(Screen.WallpaperDetail.createRoute(wallpaperId))
                    }
                )
            }
            
            composable(
                route = Screen.Settings.route,
                enterTransition = { fadeIn(tween(300)) },
                exitTransition = { fadeOut(tween(300)) }
            ) {
                SettingsScreen()
            }
            
            composable(
                route = Screen.WallpaperDetail.route,
                arguments = listOf(
                    navArgument("wallpaperId") { type = NavType.StringType }
                ),
                enterTransition = {
                    slideInVertically(initialOffsetY = { it }) + fadeIn()
                },
                exitTransition = {
                    slideOutVertically(targetOffsetY = { it }) + fadeOut()
                }
            ) { backStackEntry ->
                val wallpaperId = backStackEntry.arguments?.getString("wallpaperId") ?: ""
                WallpaperDetailScreen(
                    wallpaperId = wallpaperId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
