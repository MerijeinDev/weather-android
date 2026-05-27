package com.weather.app.data.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weather.app.data.location.LocationProvider
import com.weather.app.data.remote.NetworkModule
import com.weather.app.data.repository.WeatherRepository

/**
 * Daily background job that fetches weather for the device's current location and posts a
 * notification with today's summary.
 *
 * Scheduling, retries, and constraints are configured by [DailyWeatherScheduler];
 * this worker only does the fetch-and-post.
 *
 * Failure handling:
 *   - Location permission revoked → [Result.success] so WorkManager doesn't endlessly retry.
 *     (We'll re-schedule from MainActivity once the user grants the permission again.)
 *   - Network error → [Result.retry]; WorkManager backs off and tries again later.
 */
class DailyWeatherWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (!hasLocationPermission()) {
            Log.w(TAG, "Location permission not granted; skipping daily notification.")
            return Result.success()
        }

        return try {
            val locationProvider = LocationProvider(applicationContext)
            val coords = locationProvider.getCurrentLocation()
                ?: run {
                    Log.w(TAG, "Could not resolve current location; will retry.")
                    return Result.retry()
                }

            val repository = WeatherRepository(NetworkModule.weatherApi)
            val weather = repository.getWeather(coords.latitude, coords.longitude)

            val notification = WeatherNotifications.build(
                context = applicationContext,
                weather = weather,
                cityName = null, // reverse-geocoding can be added later
            )
            WeatherNotifications.post(applicationContext, notification)

            Result.success()
        } catch (t: Throwable) {
            Log.w(TAG, "Daily weather fetch failed: ${t.message}", t)
            Result.retry()
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private companion object {
        const val TAG = "DailyWeatherWorker"
    }
}
