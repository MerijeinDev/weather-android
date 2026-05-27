package com.weather.app.data.repository

import com.weather.app.data.remote.WeatherApi
import com.weather.app.data.remote.dto.WeatherResponseDto
import com.weather.app.domain.model.CurrentWeather
import com.weather.app.domain.model.DailyWeather
import com.weather.app.domain.model.HourlyWeather
import com.weather.app.domain.model.Weather
import com.weather.app.domain.model.WeatherCondition

class WeatherRepository(
    private val api: WeatherApi
) {
    /**
     * Fetch and map weather. Errors are bubbled up — the ViewModel decides how to display them.
     */
    suspend fun getWeather(latitude: Double, longitude: Double): Weather {
        val response = api.getForecast(latitude = latitude, longitude = longitude)
        return response.toDomain()
    }

    private fun WeatherResponseDto.toDomain(): Weather {
        val currentDomain = CurrentWeather(
            temperature = current.temperature_2m,
            feelsLike = current.apparent_temperature,
            humidity = current.relative_humidity_2m,
            windSpeed = current.wind_speed_10m,
            condition = WeatherCondition.fromCode(current.weather_code),
            isDay = current.is_day == 1
        )

        val hourlyDomain = hourly.time.indices.map { i ->
            HourlyWeather(
                time = hourly.time[i],
                temperature = hourly.temperature_2m[i],
                condition = WeatherCondition.fromCode(hourly.weather_code[i])
            )
        }

        val dailyDomain = daily.time.indices.map { i ->
            DailyWeather(
                date = daily.time[i],
                maxTemp = daily.temperature_2m_max[i],
                minTemp = daily.temperature_2m_min[i],
                condition = WeatherCondition.fromCode(daily.weather_code[i]),
                precipitation = daily.precipitation_sum[i]
            )
        }

        return Weather(
            current = currentDomain,
            hourly = hourlyDomain,
            daily = dailyDomain
        )
    }
}
