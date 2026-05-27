package com.weather.app.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Colors extracted from the Figma "Weather Forecast App" mockup.
 */
object WeatherColors {
    // Background gradient (top -> bottom)
    val SkyTop = Color(0xFF5BB6F2)
    val SkyBottom = Color(0xFF3E8FE0)

    // Glass card on top of the gradient (white with low alpha)
    val CardGlass = Color(0x33FFFFFF)
    val CardGlassBorder = Color(0x66FFFFFF)

    // Foreground / text
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xCCFFFFFF)
    val Divider = Color(0x80FFFFFF)

    // Forecast button
    val ButtonSurface = Color(0xFFFFFFFF)
    val ButtonText = Color(0xFF3E8FE0)

    // Notification dot
    val NotificationDot = Color(0xFFFF5555)

    val SkyGradient: Brush
        get() = Brush.verticalGradient(listOf(SkyTop, SkyBottom))
}
