package com.weather.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weather.app.data.location.LocationProvider
import com.weather.app.data.remote.NetworkModule
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.presentation.theme.WeatherTheme
import com.weather.app.presentation.weather.WeatherScreen
import com.weather.app.presentation.weather.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = WeatherRepository(NetworkModule.weatherApi)
                val locationProvider = LocationProvider(this@MainActivity)
                return WeatherViewModel(repository, locationProvider) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme {
                WeatherScreen(viewModel)
            }
        }
    }
}
