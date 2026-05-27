package com.weather.app.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.app.domain.model.DailyWeather
import com.weather.app.domain.model.HourlyWeather
import com.weather.app.presentation.theme.WeatherColors
import com.weather.app.presentation.theme.WeatherTypography
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Weather Detail screen — recreation of the Figma "01.02 Weather Detail" frame.
 *
 * Layout (top -> bottom):
 *   1. Top bar:        [< Back] ........... [⚙️ Settings]
 *   2. Section header: "Today" .............. "Sep, 12"
 *   3. Hourly LazyRow  (~24 future hours, "now" highlighted in a glass capsule)
 *   4. Section header: "Next Forecast" ...... [📅]
 *   5. Daily LazyColumn (date / icon / temp)
 */
@Composable
fun WeatherDetailScreen(
    state: WeatherUiState,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onCalendarClick: () -> Unit,
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
                .padding(top = 56.dp, bottom = 24.dp),
        ) {
            TopBar(onBack = onBack, onSettingsClick = onSettingsClick)

            Spacer(Modifier.height(32.dp))

            when (state) {
                WeatherUiState.Idle, WeatherUiState.Loading -> CenteredLoader(
                    modifier = Modifier.weight(1f),
                )

                is WeatherUiState.Error -> CenteredError(
                    modifier = Modifier.weight(1f),
                    message = state.message,
                    onRetry = onRetry,
                )

                is WeatherUiState.Success -> SuccessContent(
                    hourly = state.weather.hourly,
                    daily = state.weather.daily,
                    onCalendarClick = onCalendarClick,
                )
            }
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onBack),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = WeatherColors.TextPrimary,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Back",
                style = WeatherTypography.TitleBold22.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
            )
        }

        Spacer(Modifier.weight(1f))

        Icon(
            imageVector = Icons.Outlined.Settings,
            contentDescription = "Settings",
            tint = WeatherColors.TextPrimary,
            modifier = Modifier
                .size(26.dp)
                .clickable(onClick = onSettingsClick),
        )
    }
}

@Composable
private fun CenteredLoader(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = WeatherColors.TextPrimary)
    }
}

@Composable
private fun CenteredError(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = WeatherTypography.Body17,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Surface(
                onClick = onRetry,
                shape = RoundedCornerShape(20.dp),
                color = WeatherColors.ButtonSurface,
            ) {
                Text(
                    text = "Retry",
                    color = WeatherColors.ButtonText,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SuccessContent(
    hourly: List<HourlyWeather>,
    daily: List<DailyWeather>,
    onCalendarClick: () -> Unit,
) {
    val futureHours = remember(hourly) {
        val now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0)
        hourly.filter {
            runCatching { LocalDateTime.parse(it.time) }.getOrNull()
                ?.let { t -> !t.isBefore(now) } == true
        }
            .take(MAX_HOURLY)
    }

    // ── "Today" header ─────────────────────────────────────────────────────────
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Today",
            style = WeatherTypography.TitleBold22,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = todayLabel(),
            style = WeatherTypography.Body17.copy(color = WeatherColors.TextSecondary),
        )
    }

    Spacer(Modifier.height(30.dp))

    // ── Hourly LazyRow ─────────────────────────────────────────────────────────
    if (futureHours.isEmpty()) {
        Text(
            text = "No hourly data available.",
            style = WeatherTypography.Body17,
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            itemsIndexedHours(futureHours)
        }
    }

    Spacer(Modifier.height(50.dp))

    // ── "Next Forecast" header ─────────────────────────────────────────────────
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Next Forecast",
            style = WeatherTypography.TitleBold22,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = "Calendar",
            tint = WeatherColors.TextPrimary,
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onCalendarClick),
        )
    }

    Spacer(Modifier.height(15.dp))

    // ── Daily LazyColumn ───────────────────────────────────────────────────────
    // Drop today (index 0) — "Next Forecast" should start tomorrow.
    val nextDays = daily.drop(1)
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(nextDays) { day -> DailyRow(day) }
    }
}

/** Extension on LazyRow's item scope — highlights the first (current) hour. */
private fun androidx.compose.foundation.lazy.LazyListScope.itemsIndexedHours(
    hours: List<HourlyWeather>,
) {
    items(hours.size, key = { index -> hours[index].time }) { index ->
        HourCell(hour = hours[index], highlighted = index == 0)
    }
}

@Composable
private fun HourCell(hour: HourlyWeather, highlighted: Boolean) {
    val baseModifier = Modifier
        .width(64.dp)
        .height(144.dp)
        .padding(vertical = 4.dp)

    val container: @Composable (content: @Composable () -> Unit) -> Unit = { content ->
        if (highlighted) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = WeatherColors.CardGlass,
                modifier = baseModifier.border(
                    width = 1.dp,
                    color = WeatherColors.CardGlassBorder,
                    shape = RoundedCornerShape(18.dp),
                ),
            ) { content() }
        } else {
            Box(modifier = baseModifier) { content() }
        }
    }

    container {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp),
        ) {
            Text(
                text = "${hour.temperature.toInt()}°C",
                style = WeatherTypography.Body17,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = hour.condition.emoji,
                fontSize = 28.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = formatHour(hour.time),
                style = WeatherTypography.Body17.copy(fontSize = 14.sp),
            )
        }
    }
}

@Composable
private fun DailyRow(day: DailyWeather) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = formatDayLabel(day.date),
            style = WeatherTypography.Body17.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = day.condition.emoji,
            fontSize = 30.sp,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "${day.maxTemp.toInt()}°",
            style = WeatherTypography.Body17.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            ),
        )
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(28.dp)
                .background(WeatherColors.CardGlassBorder),
        )
    }
    HorizontalDivider(color = WeatherColors.Divider.copy(alpha = 0.15f))
}

// ── Helpers ─────────────────────────────────────────────────────────────────────

private const val MAX_HOURLY = 24

private val DAY_LABEL_FMT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("MMM, d", Locale.ENGLISH)

private val HOUR_FMT: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH.mm", Locale.ENGLISH)

private fun todayLabel(): String = LocalDate.now().format(DAY_LABEL_FMT)

private fun formatDayLabel(isoDate: String): String =
    runCatching { LocalDate.parse(isoDate).format(DAY_LABEL_FMT) }.getOrDefault(isoDate)

private fun formatHour(isoDateTime: String): String =
    runCatching { LocalDateTime.parse(isoDateTime).format(HOUR_FMT) }.getOrDefault(isoDateTime)
