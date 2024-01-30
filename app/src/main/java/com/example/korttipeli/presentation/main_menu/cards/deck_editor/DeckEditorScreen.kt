package com.example.korttipeli.presentation.main_menu.cards.deck_editor

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.main_menu.cards.ImagePicker
import com.example.korttipeli.presentation.main_menu.cards.deck_editor.components.NameAndPicture
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

data class DeckEditorNavArgs(val deckId: String = "")

@Destination(
    navArgsDelegate = DeckEditorNavArgs::class
)
@Composable
fun DeckEditorScreen(
    navigator: DestinationsNavigator,
    viewModel: DeckEditorViewModel = hiltViewModel(),
    appState: AppState,
) {
    val toastMessage by viewModel.toastMessage.collectAsState()
    LaunchedEffect(toastMessage) {
        if (toastMessage != "") appState.showSnackbar(toastMessage)
    }

    val navigateTo by viewModel.navigateTo.collectAsState()
    when (navigateTo) {
        NavigateTo.ImagePicker -> {
            ImagePicker { uri ->
                uri?.let { viewModel.onEvent(UiEvent.ImageChanged(it)) }
                viewModel.rogerNavigation()
            }
        }
        NavigateTo.Exit -> {
            viewModel.rogerNavigation()
            navigator.navigateUp()
        }
        NavigateTo.Nowhere -> Unit
    }


    val creationStage by viewModel.creationStage.collectAsState()
    BackHandler {
        if (creationStage is CreationStage.NameAndPicture) {
            navigator.navigateUp()
        }
        if (creationStage is CreationStage.ChoosingCards) {
            viewModel.onEvent(UiEvent.CardSelectionCanceled)
        }
        Log.i("apu", "Painoit taakse ja creation on $creationStage")
    }

    val name by viewModel.name.collectAsState()
    val bitmap = viewModel.bitmap

    if (creationStage is CreationStage.NameAndPicture) {
        NameAndPicture(
        name = name,
        bitmap = bitmap,
        viewModel = viewModel
        )
    } else {
        ChoosingCards(
            navigator = navigator,
            appState = appState,
            viewModel = viewModel
        )
    }
}