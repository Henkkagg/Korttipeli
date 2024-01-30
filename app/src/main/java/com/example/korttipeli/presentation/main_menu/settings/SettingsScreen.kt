package com.example.korttipeli.presentation.main_menu.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun SettingsScreen() {

    Box(modifier = Modifier.background(Color.White).fillMaxSize())
    Text(text = "Ollaan asetuksis :D")


}