package com.weather.app.domain.model

/**
 * Stable categorisation for tips so the UI can map them to an icon and so the database can dedupe
 * by (date, type) — see id format `"YYYY-MM-DD-{type.name}"` in WeatherTipGenerator.
 */
enum class NotificationType {
    SUNNY,
    CLOUDY,
    RAINY,
    SNOWY,
    FOGGY,
    WINDY,
    HOT,
    COLD,
    STORMY;

    companion object {
        fun fromName(name: String): NotificationType? = entries.firstOrNull { it.name == name }
    }
}
