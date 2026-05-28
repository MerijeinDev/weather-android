package com.weather.app.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.app.domain.model.CurrentWeather
import com.weather.app.presentation.theme.WeatherColors
import com.weather.app.presentation.theme.WeatherTypography
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Home screen — recreation of the Figma "01.01 Home" frame.
 *
 * Layout (top -> bottom):
 *   1. Top bar:   [📍 City ▾] ........... [🔔 with red dot]
 *   2. Big weather icon (emoji for now, swap for Meteocons SVG later)
 *   3. Glass card: date, big temperature, condition, divider, wind/humidity
 *   4. White pill button "Forecast report ▲"
 */
@Composable
fun HomeScreen(
    state: WeatherUiState,
    cityName: String,
    onNotificationClick: () -> Unit,
    onCityClick: () -> Unit,
    onForecastClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WeatherColors.SkyGradient),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopBar(
                cityName = cityName,
                onCityClick = onCityClick,
                onNotificationClick = onNotificationClick,
            )

            Spacer(Modifier.height(40.dp))

            when (state) {
                WeatherUiState.Idle, WeatherUiState.Loading -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = WeatherColors.TextPrimary)
                    }
                }
                is WeatherUiState.Error -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ErrorBlock(message = state.message, onRetry = onRetry)
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherBody(current = state.weather.current)
                    Spacer(Modifier.weight(1f))
                    ForecastReportButton(onClick = onForecastClick)
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    cityName: String,
    onCityClick: () -> Unit,
    onNotificationClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp)),
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = WeatherColors.TextPrimary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = cityName,
                style = WeatherTypography.TitleBold22,
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = "Change city",
                tint = WeatherColors.TextPrimary,
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onNotificationClick)
                .padding(4.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = WeatherColors.TextPrimary,
                modifier = Modifier.size(26.dp),
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(WeatherColors.NotificationDot),
            )
        }
    }
}

@Composable
private fun WeatherBody(current: CurrentWeather) {
    // Big weather icon (replace with painterResource(R.drawable.ic_partly_cloudy) once you add Meteocons)
    Text(
        text = current.condition.emoji,
        fontSize = 140.sp,
        textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(24.dp))

    GlassCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = todayLabel(),
                style = WeatherTypography.Body17,
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${current.temperature.toInt()}",
                    style = WeatherTypography.Display100,
                )
                Text(
                    text = "°",
                    style = WeatherTypography.Display100.copy(fontSize = 48.sp),
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            Text(
                text = current.condition.displayName,
                style = WeatherTypography.TitleBold22,
            )

            Spacer(Modifier.height(24.dp))

            MetricRow(
                icon = Icons.Outlined.WaterDrop,
                label = "Wind",
                value = "${current.windSpeed.toInt()} km/h",
            )
            Spacer(Modifier.height(12.dp))
            MetricRow(
                icon = Icons.Outlined.WaterDrop,
                label = "Hum",
                value = "${current.humidity} %",
            )
        }
    }
}

@Composable
private fun GlassCard(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = WeatherColors.CardGlass,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = WeatherColors.CardGlassBorder,
                shape = RoundedCornerShape(24.dp),
            ),
    ) {
        content()
    }
}

@Composable
private fun MetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = WeatherColors.TextPrimary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = WeatherTypography.Body17,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(18.dp)
                .background(WeatherColors.Divider),
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = value,
            style = WeatherTypography.Body17,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ForecastReportButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = WeatherColors.ButtonSurface,
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(56.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "Forecast report",
                style = WeatherTypography.Body17.copy(color = WeatherColors.ButtonText),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowUp,
                contentDescription = null,
                tint = WeatherColors.ButtonText,
            )
        }
    }
}

@Composable
private fun ErrorBlock(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = message,
            style = WeatherTypography.Body17,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = WeatherColors.ButtonSurface,
                contentColor = WeatherColors.ButtonText,
            ),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(
                text = "Retry",
                style = WeatherTypography.Body17.copy(
                    color = WeatherColors.ButtonText,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

private fun todayLabel(): String {
    val formatter = DateTimeFormatter.ofPattern("'Today,' d MMMM", Locale.ENGLISH)
    return LocalDate.now().format(formatter)
}
