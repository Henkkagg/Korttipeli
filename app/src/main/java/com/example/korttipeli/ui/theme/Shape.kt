package com.example.korttipeli.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp),
)

data class CardShapes(
    val preview: Shape = RoundedCornerShape(10.dp),
    val full: Shape = RoundedCornerShape(20.dp)
)

private val LocalShape = compositionLocalOf { CardShapes() }

val MaterialTheme.cardShapes: CardShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalShape.current