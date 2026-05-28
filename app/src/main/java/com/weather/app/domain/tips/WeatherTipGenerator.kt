package com.weather.app.domain.tips

import com.weather.app.domain.model.NotificationType
import com.weather.app.domain.model.Weather
import com.weather.app.domain.model.WeatherCondition
import com.weather.app.domain.model.WeatherNotification
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.roundToInt

/**
 * Turns a [Weather] snapshot into a deterministic list of tips for the given day.
 *
 * IDs use the format `"YYYY-MM-DD-TYPE"` so the same day + same type generates exactly one row;
 * Room's IGNORE conflict strategy then drops re-saves silently.
 *
 * Rules are intentionally simple and English-only — they can be moved into string resources or
 * a remote config when localisation is in scope.
 */
class WeatherTipGenerator(
    private val clock: Clock = Clock.systemDefaultZone(),
    private val zone: ZoneId = ZoneId.systemDefault(),
) {

    fun generate(weather: Weather): List<WeatherNotification> {
        val today = LocalDate.now(clock.withZone(zone))
        val createdAt = clock.instant()
        val tips = mutableListOf<WeatherNotification>()

        // ── Conditions-based tip ───────────────────────────────────────────────
        val conditionType = when (weather.current.condition) {
            WeatherCondition.CLEAR -> NotificationType.SUNNY
            WeatherCondition.PARTLY_CLOUDY,
            WeatherCondition.CLOUDY -> NotificationType.CLOUDY
            WeatherCondition.DRIZZLE,
            WeatherCondition.RAIN -> NotificationType.RAINY
            WeatherCondition.SNOW -> NotificationType.SNOWY
            WeatherCondition.FOG -> NotificationType.FOGGY
            WeatherCondition.THUNDERSTORM -> NotificationType.STORMY
            WeatherCondition.UNKNOWN -> null
        }
        if (conditionType != null) {
            tips += build(today, createdAt, conditionType, messageFor(conditionType, weather))
        }

        // ── Temperature extremes ──────────────────────────────────────────────
        val temp = weather.current.temperature
        when {
            temp < COLD_THRESHOLD -> tips += build(
                today, createdAt, NotificationType.COLD,
                "It's chilly today (${temp.roundToInt()}°C). Bundle up before heading out.",
            )
            temp > HOT_THRESHOLD -> tips += build(
                today, createdAt, NotificationType.HOT,
                "Hot day ahead (${temp.roundToInt()}°C). Stay hydrated and avoid direct sun.",
            )
        }

        // ── Wind ──────────────────────────────────────────────────────────────
        if (weather.current.windSpeed >= WINDY_THRESHOLD_KMH) {
            tips += build(
                today, createdAt, NotificationType.WINDY,
                "Strong winds today (${weather.current.windSpeed.roundToInt()} km/h). " +
                    "Secure loose items outdoors.",
            )
        }

        return tips
    }

    private fun build(
        date: LocalDate,
        createdAt: java.time.Instant,
        type: NotificationType,
        message: String,
    ) = WeatherNotification(
        id = "$date-${type.name}",
        createdAt = createdAt,
        type = type,
        message = message,
    )

    private fun messageFor(type: NotificationType, weather: Weather): String = when (type) {
        NotificationType.SUNNY -> "A sunny day in your location. Consider wearing UV protection."
        NotificationType.CLOUDY ->
            "A cloudy day will occur. Don't worry about the heat of the sun."
        NotificationType.RAINY -> {
            val precip = weather.daily.firstOrNull()?.precipitation ?: 0.0
            if (precip > 0) {
                "Rain expected today (~${precip.roundToInt()} mm). " +
                    "Don't forget to bring your umbrella."
            } else {
                "Rain is expected today. Don't forget to bring your umbrella."
            }
        }
        NotificationType.SNOWY ->
            "Snow on the way. Drive carefully and dress warmly."
        NotificationType.FOGGY ->
            "Foggy conditions today — reduced visibility, drive carefully."
        NotificationType.STORMY ->
            "Thunderstorms expected today. Stay indoors if possible."
        NotificationType.HOT,
        NotificationType.COLD,
        NotificationType.WINDY -> ""
    }

    private companion object {
        const val COLD_THRESHOLD = 5.0      // °C
        const val HOT_THRESHOLD = 30.0      // °C
        const val WINDY_THRESHOLD_KMH = 30.0 // km/h
    }
}
