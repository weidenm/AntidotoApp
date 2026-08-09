package com.antidoto.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.antidoto.R
import com.antidoto.ui.screens.CheckInScreen
import com.antidoto.ui.screens.GoalsScreen
import com.antidoto.ui.screens.HomeScreen
import com.antidoto.ui.screens.LessonDetailScreen
import com.antidoto.ui.screens.LessonsTrailScreen
import com.antidoto.ui.screens.SettingsScreen
import com.antidoto.ui.viewmodels.LessonDetailViewModel

enum class Destination(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector,
) {
    HOME("home", R.string.nav_home, Icons.Filled.Home),
    CHECKIN("checkin", R.string.nav_checkin, Icons.Filled.Favorite),
    GOALS("goals", R.string.nav_goals, Icons.Filled.DateRange),
    LESSONS("lessons", R.string.nav_lessons, Icons.Filled.List),
    SETTINGS("settings", R.string.nav_settings, Icons.Filled.Settings),
}

@Composable
fun AntidotoApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            NavigationBar {
                Destination.entries.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == destination.route
                    } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(stringResource(destination.label)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Destination.HOME.route) { HomeScreen(viewModel = hiltViewModel()) }
            composable(Destination.CHECKIN.route) { CheckInScreen(viewModel = hiltViewModel()) }
            composable(Destination.GOALS.route) { GoalsScreen(viewModel = hiltViewModel()) }
            composable(Destination.LESSONS.route) {
                LessonsTrailScreen(
                    viewModel = hiltViewModel(),
                    onOpenLesson = { lessonId -> navController.navigate("lesson/$lessonId") },
                )
            }
            composable(Destination.SETTINGS.route) { SettingsScreen(viewModel = hiltViewModel()) }
            composable(
                route = "lesson/{${LessonDetailViewModel.ARG_LESSON_ID}}",
                arguments = listOf(
                    navArgument(LessonDetailViewModel.ARG_LESSON_ID) { type = NavType.StringType },
                ),
            ) {
                LessonDetailScreen(
                    viewModel = hiltViewModel(),
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}
