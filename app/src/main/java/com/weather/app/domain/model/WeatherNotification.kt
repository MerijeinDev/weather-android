package com.weather.app.domain.model

import java.time.Instant

/**
 * Domain representation of an in-app weather tip / notification entry.
 * Stored as `data/local/NotificationEntity` and surfaced on the Notifications screen.
 */
data class WeatherNotification(
    val id: String,
    val createdAt: Instant,
    val type: NotificationType,
    val message: String,
)
