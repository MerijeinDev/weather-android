package com.weather.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weather.app.presentation.weather.HomeScreenRoot
import com.weather.app.presentation.weather.WeatherDetailScreen
import com.weather.app.presentation.weather.WeatherViewModel

/**
 * Single NavHost for the app. The shared [WeatherViewModel] is passed down to both screens so
 * Home and Detail render the same [com.weather.app.presentation.weather.WeatherUiState].
 */
@Composable
fun WeatherNavGraph(viewModel: WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreenRoot(
                viewModel = viewModel,
                onForecastClick = { navController.navigate(DetailRoute) },
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
    }
}
