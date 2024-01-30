package com.example.korttipeli.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Blue = Color(0xFF67e1dd)
val BackgroundBlue = Color(0xFF77D2FF)
val ButtonBlue = Color(0xFF44C1FF)
val DarkButtonBlue = Color(0xFF0094DE)

val White = Color(0xFFFFFFFF)
val Yellow = Color(0xFFf8ffa8)

//Card colors
val Grey = Color(0xFFD4D4D4)
val BorderGrey = Color(0xFF434343)

val Green = Color(0xFFa7f542)
val BorderGreen = Color(0xFF00701A)

val Pink = Color(0xFFf486ff)
val BorderPink = Color(0xFFEF00FF)




val MaterialTheme.normalOutlinedTextFieldColors: TextFieldColors
    @Composable
    get() = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = customTextFieldColor,
        focusedBorderColor = customTextFieldColor,
        focusedLabelColor = Color.Black,
        trailingIconColor = customTextFieldColor
    )
val MaterialTheme.unsatisfiedOutlinedTextFieldColors: TextFieldColors
    @Composable
    get() = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = customTextFieldColor,
        focusedBorderColor = customTextFieldColor,
        focusedLabelColor = Color.Black,
        trailingIconColor = customTextFieldColor,
        backgroundColor = Grey
    )

private val customTextFieldColor = DarkButtonBlue
val customTextSelectionColors = TextSelectionColors(
    handleColor = customTextFieldColor,
    backgroundColor = customTextFieldColor.copy(alpha = 0.4f)
)