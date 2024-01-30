package com.example.korttipeli.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val default: Dp = 0.dp,
    val border: Dp = 3.dp,
    val small: Dp = 6.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 20.dp,
    val extraLarge: Dp = 30.dp
)

private val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current