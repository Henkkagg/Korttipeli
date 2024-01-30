package com.example.korttipeli.presentation.main_menu.cards.deck_editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.korttipeli.R
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.common_components.CustomButton
import com.example.korttipeli.presentation.main_menu.cards.CardsScreen
import com.example.korttipeli.ui.theme.Green
import com.example.korttipeli.ui.theme.Grey
import com.example.korttipeli.ui.theme.Pink
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun ChoosingCards(
    navigator: DestinationsNavigator,
    appState: AppState,
    viewModel: DeckEditorViewModel
) {
    val cardTypes by viewModel.cardTypes.collectAsState()
    val creationStage by viewModel.creationStage.collectAsState()

    //If we get here by pressing edit on existing deck
    val preselectedCards by viewModel.cardIds.collectAsState()

    Column {

        Row {
            val modifier = Modifier
                .fillMaxWidth()
                .weight(1f)

            TypeCounter(
                modifier = modifier,
                R.drawable.ic_action,
                cardTypes[0],
                Grey
            )
            TypeCounter(
                modifier = modifier,
                R.drawable.ic_virus,
                cardTypes[1],
                Green
            )
            TypeCounter(
                modifier = modifier,
                R.drawable.ic_secret,
                cardTypes[2],
                Pink
            )
        }

        CardsScreen(
            navigator = navigator,
            appState = appState,
            selectionMode = true,
            preselectedCards = preselectedCards,
            modifier = Modifier.weight(1f),
            onBackPressed = { viewModel.onEvent(UiEvent.CardSelectionCanceled) },
            onSelectionChanged = { viewModel.onEvent(UiEvent.SelectionChanged(it)) }
        )

        CustomButton(
            enabled = cardTypes.any { it > 0 } && creationStage != CreationStage.Processing,
            text = if (creationStage != CreationStage.Processing) "Tallenna pakka" else "Ladataan"
        ) {
            viewModel.onEvent(UiEvent.ConfirmPressed)
        }
    }
}

@Composable
fun TypeCounter(
    modifier: Modifier,
    icon: Int,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    verticalMode: Boolean = false
) {
    if (!verticalMode) {
        Row(
            modifier = modifier
                .background(color),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Ikoni",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("$count")
        }
    } else {
        Column(
            modifier = modifier
                .background(color),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Ikoni",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("$count")
        }
    }
}