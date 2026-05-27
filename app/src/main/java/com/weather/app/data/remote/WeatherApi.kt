package com.weather.app.data.remote

import com.weather.app.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    /**
     * Open-Meteo forecast endpoint — no API key required.
     * Example call: /v1/forecast?latitude=52.52&longitude=13.41&current=...&daily=...
     */
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("hourly") hourly: String = HOURLY_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherResponseDto

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"

        private const val CURRENT_FIELDS =
            "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code,is_day"

        private const val HOURLY_FIELDS =
            "temperature_2m,weather_code"

        private const val DAILY_FIELDS =
            "temperature_2m_max,temperature_2m_min,weather_code,precipitation_sum"
    }
}
