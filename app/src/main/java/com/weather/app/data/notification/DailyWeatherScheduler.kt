package com.weather.app.data.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Manages the daily weather notification job.
 *
 * Scheduling rules:
 *  - Repeats every 24h (the WorkManager minimum period is 15 minutes, 24h is comfortably above).
 *  - First run lands at the next 08:00 local time, computed via [computeInitialDelayMillis].
 *  - Requires network — the worker would otherwise fail the API call and retry forever.
 *  - Uses [ExistingPeriodicWorkPolicy.KEEP] so re-launching the app doesn't reset the schedule.
 */
object DailyWeatherScheduler {

    const val UNIQUE_WORK_NAME = "daily_weather_forecast_work"

    /** Target time-of-day for the morning notification. Change here to retune. */
    private val NOTIFY_AT: LocalTime = LocalTime.of(8, 0)

    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<DailyWeatherWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            .setConstraints(constraints)
            .setInitialDelay(computeInitialDelayMillis(NOTIFY_AT), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext)
            .cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    /**
     * Milliseconds until the next [target] local time.
     * If [target] has already passed today, schedule for tomorrow.
     */
    internal fun computeInitialDelayMillis(
        target: LocalTime,
        now: LocalDateTime = LocalDateTime.now(),
    ): Long {
        val targetToday = LocalDateTime.of(LocalDate.from(now), target)
        val firstFire = if (now.isBefore(targetToday)) targetToday else targetToday.plusDays(1)
        return Duration.between(now, firstFire).toMillis().coerceAtLeast(0)
    }
}
