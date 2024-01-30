package com.example.korttipeli.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun JuomapeliTheme(content: @Composable () -> Unit) {

    val colors = lightColors(
        primary = Blue,
        primaryVariant = Pink,
        secondary = ButtonBlue,
        secondaryVariant = Pink,
        background = BackgroundBlue
    )


    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}