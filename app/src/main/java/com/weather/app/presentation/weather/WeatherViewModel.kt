package com.weather.app.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.app.data.location.LocationProvider
import com.weather.app.data.repository.NotificationRepository
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.domain.model.Weather
import com.weather.app.domain.tips.WeatherTipGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val locationProvider: LocationProvider,
    private val notificationRepository: NotificationRepository,
    private val tipGenerator: WeatherTipGenerator = WeatherTipGenerator(),
) : ViewModel() {

    private val _state = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    fun loadWeatherForCurrentLocation() {
        viewModelScope.launch {
            _state.value = WeatherUiState.Loading
            try {
                val coords = locationProvider.getCurrentLocation()
                if (coords == null) {
                    _state.value = WeatherUiState.Error("Could not determine location")
                    return@launch
                }
                val weather = repository.getWeather(coords.latitude, coords.longitude)
                _state.value = WeatherUiState.Success(weather)
                saveTips(weather)
            } catch (t: Throwable) {
                _state.value = WeatherUiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    /**
     * Generate tips for the current snapshot and save them. Errors here MUST NOT bubble up to the
     * UI — failing to write the tips inbox shouldn't break the weather screen.
     */
    private suspend fun saveTips(weather: Weather) {
        runCatching {
            val tips = tipGenerator.generate(weather)
            notificationRepository.saveAll(tips)
        }
    }
}
