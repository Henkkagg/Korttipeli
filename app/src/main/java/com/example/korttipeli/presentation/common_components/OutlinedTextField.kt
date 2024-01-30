package com.example.korttipeli.presentation.common_components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.input.*
import com.example.korttipeli.ui.theme.customTextSelectionColors
import com.example.korttipeli.ui.theme.normalOutlinedTextFieldColors
import com.example.korttipeli.ui.theme.unsatisfiedOutlinedTextFieldColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    label: String,
    singleLine: Boolean = true,
    inputAccepted: Boolean = true,
    keyboardSettings: KeyboardSettings = KeyboardSettings.Normal,
    imeAction: ImeAction = ImeAction.Default,
    bringIntoViewRequester: BringIntoViewRequester? = null,
    onImeActionPressed: () -> Unit = {},
    onValueChange: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        OutlinedTextField(
            value = value,
            label = { Text(text = label) },
            colors = if (inputAccepted) {
                MaterialTheme.normalOutlinedTextFieldColors
            } else {
                MaterialTheme.unsatisfiedOutlinedTextFieldColors
            },
            singleLine = singleLine,
            visualTransformation = when (keyboardSettings) {
                is KeyboardSettings.Password -> {
                    PasswordVisualTransformation()
                }
                else -> {
                    VisualTransformation.None
                }
            },
            keyboardOptions = when (keyboardSettings) {
                is KeyboardSettings.Normal -> {
                    KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences)
                }
                is KeyboardSettings.Email -> {
                    KeyboardOptions(keyboardType = KeyboardType.Email)
                }
                is KeyboardSettings.Password -> {
                    KeyboardOptions(keyboardType = KeyboardType.Password)
                }
            }.copy(
                imeAction = imeAction
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent {
                    if (bringIntoViewRequester != null) {
                        coroutineScope.launch {
                            delay(100)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            onValueChange = {
                onValueChange(it)
            },
            keyboardActions = KeyboardActions(onGo = { onImeActionPressed() })
        )
    }
}

sealed class KeyboardSettings {
    object Normal : KeyboardSettings()
    object Password : KeyboardSettings()
    object Email : KeyboardSettings()
}
