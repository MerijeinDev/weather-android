package com.weather.app.presentation.weather

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.weather.app.presentation.theme.WeatherColors

/**
 * Stateful wrapper around [HomeScreen]:
 *  - collects state from [WeatherViewModel]
 *  - asks for location permission
 *  - kicks off the first weather load when permission is granted
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenRoot(
    viewModel: WeatherViewModel,
    cityName: String = "My location",
    onNotificationClick: () -> Unit = {},
    onCityClick: () -> Unit = {},
    onForecastClick: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)

    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            viewModel.loadWeatherForCurrentLocation()
        }
    }

    if (!locationPermission.status.isGranted) {
        PermissionGate(onGrant = { locationPermission.launchPermissionRequest() })
        return
    }

    HomeScreen(
        state = state,
        cityName = cityName,
        onNotificationClick = onNotificationClick,
        onCityClick = onCityClick,
        onForecastClick = onForecastClick,
        onRetry = { viewModel.loadWeatherForCurrentLocation() },
    )
}

@Composable
private fun PermissionGate(onGrant: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WeatherColors.SkyGradient),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "We need your location to show the weather.",
                color = WeatherColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onGrant,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WeatherColors.ButtonSurface,
                    contentColor = WeatherColors.ButtonText,
                ),
            ) {
                Text("Grant permission", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
