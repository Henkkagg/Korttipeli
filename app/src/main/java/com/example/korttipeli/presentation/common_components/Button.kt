package com.example.korttipeli.presentation.common_components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.korttipeli.ui.theme.ButtonBlue
import com.example.korttipeli.ui.theme.Grey

@Composable
fun CustomButton(
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = ButtonBlue,
            disabledBackgroundColor = Grey
        ),
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(
            text = text,
            color = Color.Black
        )
    }
}