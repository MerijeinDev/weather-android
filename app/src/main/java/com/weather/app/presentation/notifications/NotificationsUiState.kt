package com.weather.app.presentation.notifications

import com.weather.app.domain.model.WeatherNotification

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val newItems: List<WeatherNotification> = emptyList(),
    val earlierItems: List<WeatherNotification> = emptyList(),
) {
    val isEmpty: Boolean get() = newItems.isEmpty() && earlierItems.isEmpty()
}
