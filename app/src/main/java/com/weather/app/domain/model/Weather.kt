package com.weather.app.domain.model

/**
 * UI/Domain-friendly weather model — decoupled from the network DTOs.
 */
data class Weather(
    val current: CurrentWeather,
    val daily: List<DailyWeather>
)

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val condition: WeatherCondition,
    val isDay: Boolean
)

data class DailyWeather(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val condition: WeatherCondition,
    val precipitation: Double
)

enum class WeatherCondition(val displayName: String, val emoji: String) {
    CLEAR("Clear sky", "☀️"),
    PARTLY_CLOUDY("Partly cloudy", "⛅"),
    CLOUDY("Cloudy", "☁️"),
    FOG("Fog", "🌫️"),
    DRIZZLE("Drizzle", "🌦️"),
    RAIN("Rain", "🌧️"),
    SNOW("Snow", "❄️"),
    THUNDERSTORM("Thunderstorm", "⛈️"),
    UNKNOWN("Unknown", "❓");

    companion object {
        // WMO weather codes mapping. See https://open-meteo.com/en/docs
        fun fromCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR
            1, 2 -> PARTLY_CLOUDY
            3 -> CLOUDY
            45, 48 -> FOG
            51, 53, 55, 56, 57 -> DRIZZLE
            61, 63, 65, 66, 67, 80, 81, 82 -> RAIN
            71, 73, 75, 77, 85, 86 -> SNOW
            95, 96, 99 -> THUNDERSTORM
            else -> UNKNOWN
        }
    }
}
