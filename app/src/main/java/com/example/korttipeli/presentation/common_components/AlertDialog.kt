package com.example.korttipeli.presentation.common_components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.korttipeli.ui.theme.BackgroundBlue
import com.example.korttipeli.ui.theme.ButtonBlue

@Composable
fun CustomAlertDialog(
    title: String = "Oletko varma?",
    message: String,
    onConfirmText: String,
    onDismissText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        buttons = {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onConfirm() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = ButtonBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(10.dp)
                ) {
                    Text(onConfirmText)
                }
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = ButtonBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(10.dp)
                ) {
                    Text(onDismissText)
                }
            }
        },
        backgroundColor = BackgroundBlue,
    )
}