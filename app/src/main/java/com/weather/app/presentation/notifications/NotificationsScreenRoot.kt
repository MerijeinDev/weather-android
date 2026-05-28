package com.weather.app.presentation.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/**
 * Stateful wrapper for [NotificationsScreen]. The ViewModel is created/owned by the caller
 * (see [com.weather.app.presentation.navigation.WeatherNavGraph]) so it can be scoped to the
 * NavBackStackEntry.
 */
@Composable
fun NotificationsScreenRoot(
    viewModel: NotificationsViewModel,
    onClose: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    NotificationsScreen(state = state, onClose = onClose)
}
