package com.weather.app.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.weather.app.R

val OverpassFontFamily = FontFamily(
    Font(R.font.overpass_regular, FontWeight.Normal),
    Font(R.font.overpass_bold, FontWeight.Bold),
)

object WeatherTypography {
    val Body17 = TextStyle(
        fontFamily = OverpassFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        color = WeatherColors.TextPrimary,
    )

    val Display100 = TextStyle(
        fontFamily = OverpassFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 100.sp,
        color = WeatherColors.TextPrimary,
    )

    val TitleBold22 = TextStyle(
        fontFamily = OverpassFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = WeatherColors.TextPrimary,
    )
}