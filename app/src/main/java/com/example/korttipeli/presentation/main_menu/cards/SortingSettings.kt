package com.example.korttipeli.presentation.main_menu.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.korttipeli.ui.theme.BackgroundBlue
import com.example.korttipeli.ui.theme.ButtonBlue

@Composable
fun SortingSettings(viewModel: CardsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundBlue)
    ) {
        val textStyle = MaterialTheme.typography.body1

        SortingRadioOption(
            "Aakkosjärjestys",
            textStyle,
            viewModel.sortingSetting.ascending
        ) {
            viewModel.onEvent(UiEvent.SortAscendingToggled)
        }
        SortingRadioOption(
            "Käänteinen aakkosjärjestys",
            textStyle,
            !viewModel.sortingSetting.ascending
        ) {
            viewModel.onEvent(UiEvent.SortAscendingToggled)
        }
        SortingToggleOption(
            "Itse tehdyt päällimmäisenä",
            textStyle,
            viewModel.sortingSetting.showOwnOnTop
        ) {
            viewModel.onEvent(UiEvent.OwnOnTopToggled)
        }
        SortingToggleOption(
            "Erottele korttityypit",
            textStyle,
            viewModel.sortingSetting.separateTypes
        ) {
            viewModel.onEvent(UiEvent.TypesSeparatedToggled)
        }
        SortingToggleOption(
            "Erottele korttien tekijät",
            textStyle,
            viewModel.sortingSetting.separateAuthors
        ) {
            viewModel.onEvent(UiEvent.AuthorsSeparatedToggled)
        }
    }
}

@Composable
private fun SortingRadioOption(
    text: String,
    textStyle: TextStyle,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text, style = textStyle)
        RadioButton(
            selected = selected,
            onClick = onToggle,
            colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
        )
    }
}

@Composable
private fun SortingToggleOption(
    text: String,
    textStyle: TextStyle,
    selected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text, style = textStyle)
        Checkbox(
            checked = selected,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(
                checkmarkColor = Color.Black,
                checkedColor = ButtonBlue
            )
        )
    }
}