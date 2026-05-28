package com.weather.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weather.app.data.local.DatabaseModule
import com.weather.app.data.location.LocationProvider
import com.weather.app.data.notification.DailyWeatherScheduler
import com.weather.app.data.notification.WeatherNotifications
import com.weather.app.data.remote.NetworkModule
import com.weather.app.data.repository.NotificationRepository
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.presentation.navigation.WeatherNavGraph
import com.weather.app.presentation.notifications.NotificationsViewModel
import com.weather.app.presentation.theme.WeatherTheme
import com.weather.app.presentation.weather.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val database by lazy { DatabaseModule.provide(applicationContext) }
    private val notificationRepository by lazy {
        NotificationRepository(database.notificationDao())
    }

    private val viewModel: WeatherViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = WeatherRepository(NetworkModule.weatherApi)
                val locationProvider = LocationProvider(this@MainActivity)
                return WeatherViewModel(
                    repository = repository,
                    locationProvider = locationProvider,
                    notificationRepository = notificationRepository,
                ) as T
            }
        }
    }

    private val notificationsViewModel: NotificationsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationsViewModel(notificationRepository) as T
            }
        }
    }

    /**
     * Android 13+ requires runtime POST_NOTIFICATIONS permission. We ask once on launch;
     * if denied, the daily worker still runs but the notification will be silently dropped
     * (handled by [WeatherNotifications.post]).
     */
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* result intentionally unused */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WeatherNotifications.ensureChannel(this)
        requestNotificationPermissionIfNeeded()
        DailyWeatherScheduler.schedule(this)

        setContent {
            WeatherTheme {
                WeatherNavGraph(
                    viewModel = viewModel,
                    notificationsViewModel = notificationsViewModel,
                )
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED) return
        notificationPermissionLauncher.launch(permission)
    }
}
