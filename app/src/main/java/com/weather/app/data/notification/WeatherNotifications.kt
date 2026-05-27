package com.weather.app.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.weather.app.MainActivity
import com.weather.app.R
import com.weather.app.domain.model.Weather

/**
 * Centralised notification setup for daily weather updates.
 *
 *  - Channel lives in [CHANNEL_ID]; created once, idempotent.
 *  - Title / body are built from a [Weather] snapshot.
 *  - Tapping the notification opens [MainActivity].
 *  - Posting respects the runtime POST_NOTIFICATIONS permission via [NotificationManagerCompat.areNotificationsEnabled].
 */
object WeatherNotifications {

    const val CHANNEL_ID = "daily_weather_forecast"
    const val NOTIFICATION_ID = 1001

    /** Create the channel on Android 8+. Safe to call multiple times. */
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService<NotificationManager>() ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notif_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notif_channel_description)
            enableLights(false)
            enableVibration(true)
            setShowBadge(true)
        }
        manager.createNotificationChannel(channel)
    }

    /** Build today's-weather notification from the latest weather snapshot. */
    fun build(context: Context, weather: Weather, cityName: String?): android.app.Notification {
        ensureChannel(context)

        val today = weather.daily.firstOrNull()
        val current = weather.current

        val title = if (cityName.isNullOrBlank()) {
            context.getString(R.string.notif_title_no_city)
        } else {
            context.getString(R.string.notif_title, cityName)
        }

        val body = buildString {
            append(current.condition.emoji).append("  ")
            append("${current.temperature.toInt()}°C, ")
            append(current.condition.displayName.replaceFirstChar { it.lowercase() })
            if (today != null) {
                append(".  ")
                append(
                    context.getString(
                        R.string.notif_body_high_low,
                        today.maxTemp.toInt(),
                        today.minTemp.toInt(),
                    ),
                )
            }
        }

        val openAppIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(openAppIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    /** Posts a notification; silently does nothing if user disabled notifications. */
    fun post(context: Context, notification: android.app.Notification) {
        val nm = NotificationManagerCompat.from(context)
        if (!nm.areNotificationsEnabled()) return
        try {
            nm.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS not granted on Android 13+. Swallow — user can grant later.
        }
    }
}
