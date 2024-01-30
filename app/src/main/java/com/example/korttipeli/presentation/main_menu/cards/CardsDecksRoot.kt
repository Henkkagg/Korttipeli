package com.example.korttipeli.presentation.main_menu.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.presentation.app.AppState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CardsDecksRoot(
    navigator: DestinationsNavigator,
    viewModel: CardsDecksRootViewModel = hiltViewModel(),
    appState: AppState,
) {
    val cardsSelected by viewModel.cardsSelected.collectAsState()
    val decksSelected by viewModel.decksSelected.collectAsState()

    Column {
        SelectionToggleSection(
            cardsSelected = cardsSelected,
            onCardsPressed = { viewModel.cardsPressed() },
            decksSelected = decksSelected,
            onDecksPressed = { viewModel.decksPressed() }
        )

        if (cardsSelected) CardsScreen(
            navigator = navigator,
            appState = appState
        )

        if (decksSelected) DecksScreen(
            navigator = navigator,
            appState = appState
        )
    }
}

@Composable
fun SelectionToggleSection(
    cardsSelected: Boolean,
    onCardsPressed: (Boolean) -> Unit,
    decksSelected: Boolean,
    onDecksPressed: (Boolean) -> Unit
) {
    Box(contentAlignment = Alignment.CenterEnd) {
        Row {
            IconToggleButton(
                checked = cardsSelected,
                enabled = !cardsSelected,
                onCheckedChange = onCardsPressed,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Kortit",
                    color = if (cardsSelected) Color.Black else Color(0f, 0f, 0f, 0.5f)
                )
            }
            IconToggleButton(
                checked = decksSelected,
                enabled = !decksSelected,
                onCheckedChange = onDecksPressed,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Pakat",
                    color = if (decksSelected) Color.Black else Color(0f, 0f, 0f, 0.5f)
                )
            }
        }
    }
}