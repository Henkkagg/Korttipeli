package com.example.korttipeli.presentation.main_menu.play.create_lobby

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.korttipeli.domain.model.NewGame
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.common_components.CustomButton
import com.example.korttipeli.presentation.common_components.CustomOutlinedTextField
import com.example.korttipeli.presentation.destinations.GameScreenDestination
import com.example.korttipeli.presentation.main_menu.cards.DeckPreview
import com.example.korttipeli.presentation.main_menu.cards.DecksScreen
import com.example.korttipeli.ui.theme.Grey
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.example.korttipeli.R

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun CreateLobbyScreen(
    navigator: DestinationsNavigator,
    viewModel: CreateLobbyViewModel = hiltViewModel(),
    appState: AppState
) {
    BackHandler {
        viewModel.onEvent(UiEvent.BackPressed)
    }

    val name by viewModel.name.collectAsState()
    val conditionsMet by viewModel.conditionsMet.collectAsState()
    val inSelectionMode by viewModel.inSelectionMode.collectAsState()

    val navigateTo by viewModel.navigateTo.collectAsState()
    LaunchedEffect(navigateTo) {
        when (navigateTo) {
            NavigateTo.Back -> navigator.navigateUp()
            NavigateTo.Game -> {
                navigator.popBackStack()
                navigator.navigate(
                    GameScreenDestination(
                        newGame = NewGame(viewModel.deck!!.id, name)
                    )
                )

            }
            NavigateTo.Nowhere -> {}
        }
        viewModel.rogerNavigation()
    }
    val toast by viewModel.toast.collectAsState()
    LaunchedEffect(toast) {
        if (toast != "") appState.showSnackbar(toast)
    }


    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {

        if (!inSelectionMode) {

            Column {

                CustomOutlinedTextField(
                    value = name,
                    label = "Pelin nimi",
                    inputAccepted = name.isNotBlank(),
                    onValueChange = { viewModel.onEvent(UiEvent.NameTyped(it)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (viewModel.deck == null) {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.placeholder_choosedeck),
                        contentDescription = "Pakan kuva",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(3.dp, Grey, RoundedCornerShape(20.dp))
                            .aspectRatio(1.5f)
                            .clickable { viewModel.onEvent(UiEvent.SelectDeckPressed) },
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    DeckPreview(deck = viewModel.deck!!, image = viewModel.bitmap!!) {
                        viewModel.onEvent(UiEvent.SelectDeckPressed)
                    }
                }
            }

            CustomButton(
                enabled = conditionsMet,
                text = "Luo"
            ) {
                viewModel.onEvent(UiEvent.ReadyPressed)
            }
        }

        if (inSelectionMode) {
            Text(
                text = "Valitse pakka",
                style = MaterialTheme.typography.h1
            )
            DecksScreen(
                navigator = navigator,
                appState = appState,
                creatingGame = true,
                onPressed = { viewModel.onEvent(UiEvent.DeckSelected(it)) }
            )
        }

    }
}