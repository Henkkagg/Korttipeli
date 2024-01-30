package com.example.korttipeli.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.korttipeli.R

private val fonts = FontFamily(
    Font(R.font.regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.extrabold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic)
)

val Typography = Typography(

    body1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 20.sp
    ),
    h1 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal,
        fontSize = 25.sp
    ),
    h2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.ExtraBold,
        fontStyle = FontStyle.Italic,
        fontSize = 25.sp
    ),
    h3 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 30.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = fonts,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 12.sp
    )
)