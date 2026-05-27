package com.weather.app.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Response shape from Open-Meteo Forecast API.
 * Docs: https://open-meteo.com/en/docs
 */
@Serializable
data class WeatherResponseDto(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeatherDto,
    val hourly: HourlyWeatherDto,
    val daily: DailyWeatherDto
)

@Serializable
data class HourlyWeatherDto(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>
)

@Serializable
data class CurrentWeatherDto(
    val time: String,
    val temperature_2m: Double,
    val apparent_temperature: Double,
    val relative_humidity_2m: Int,
    val wind_speed_10m: Double,
    val weather_code: Int,
    val is_day: Int
)

@Serializable
data class DailyWeatherDto(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val weather_code: List<Int>,
    val precipitation_sum: List<Double>
)
