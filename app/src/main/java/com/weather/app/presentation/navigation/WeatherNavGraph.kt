package com.weather.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weather.app.presentation.notifications.NotificationsScreenRoot
import com.weather.app.presentation.notifications.NotificationsViewModel
import com.weather.app.presentation.weather.HomeScreenRoot
import com.weather.app.presentation.weather.WeatherDetailScreen
import com.weather.app.presentation.weather.WeatherViewModel

/**
 * Single NavHost for the app. ViewModels are owned by [com.weather.app.MainActivity] so they
 * survive config changes and are shared between destinations that need the same state.
 */
@Composable
fun WeatherNavGraph(
    viewModel: WeatherViewModel,
    notificationsViewModel: NotificationsViewModel,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreenRoot(
                viewModel = viewModel,
                onForecastClick = { navController.navigate(DetailRoute) },
                onNotificationClick = { navController.navigate(NotificationsRoute) },
            )
        }

        composable<DetailRoute> {
            val state by viewModel.state.collectAsState()
            WeatherDetailScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onSettingsClick = { /* TODO: settings route */ },
                onCalendarClick = { /* TODO: full calendar route */ },
                onRetry = { viewModel.loadWeatherForCurrentLocation() },
            )
        }

        composable<NotificationsRoute> {
            NotificationsScreenRoot(
                viewModel = notificationsViewModel,
                onClose = { navController.popBackStack() },
            )
        }
    }
}
