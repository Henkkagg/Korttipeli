package com.example.korttipeli.presentation.main_menu.cards.deck_editor.deck_inspector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.domain.model.CardUi
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.common_components.CustomAlertDialog
import com.example.korttipeli.presentation.common_components.CustomButton
import com.example.korttipeli.presentation.destinations.CardInspectorScreenDestination
import com.example.korttipeli.presentation.destinations.DeckEditorScreenDestination
import com.example.korttipeli.presentation.main_menu.cards.CardPreview
import com.example.korttipeli.presentation.main_menu.cards.DeckPreview
import com.example.korttipeli.ui.theme.DarkButtonBlue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

data class DeckInspectorNavArgs(
    val id: String
)

@Destination(
    navArgsDelegate = DeckInspectorNavArgs::class
)
@Composable
fun DeckInspector(
    navigator: DestinationsNavigator,
    viewModel: DeckInspectorViewModel = hiltViewModel(),
    appState: AppState
) {
    LaunchedEffect(Unit) {
        viewModel.onInit()
    }

    LaunchedEffect(viewModel.toastMessage) {
        if (viewModel.toastMessage != "") appState.showSnackbar(viewModel.toastMessage)
    }

    if (viewModel.navigateTo is NavigateTo.Editor) {
        viewModel.rogerNavigation()
        navigator.navigate(DeckEditorScreenDestination(viewModel.deck.id))
    } else if (viewModel.navigateTo is NavigateTo.Exit) {
        viewModel.rogerNavigation()
        navigator.navigateUp()
    }

    if (viewModel.shouldShowAlertDialog) CustomAlertDialog(
        message = "Poistetaanko pakka?",
        onConfirmText = "Kyllä",
        onDismissText = "Peruuta",
        onDismiss = { viewModel.onEvent(UiEvent.DeleteCanceled) }) {
        viewModel.onEvent(UiEvent.DeleteConfirmed)
    }


    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DeckPreview(
                    deck = viewModel.deck,
                    image = viewModel.bitmap
                )
                Text(
                    text = "- ${viewModel.deck.author} -",
                    style = MaterialTheme.typography.subtitle2
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (viewModel.shouldBeEditable) {
                    Row {
                        CustomButton(
                            enabled = true,
                            text = "Muokkaa pakkaa",
                            modifier = Modifier.weight(1f)
                        ) { viewModel.onEvent(UiEvent.EditPressed) }
                        IconButton(
                            onClick = { viewModel.onEvent(UiEvent.DeletePressed) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Poista",
                                tint = DarkButtonBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

        }

        items(viewModel.cards) { card ->
            CardPreview(
                cardUi = CardUi(
                    id = card.id,
                    title = card.title,
                    image = viewModel.loadImage(card.image),
                    type = card.type
                )
            ) { navigator.navigate(CardInspectorScreenDestination(card.id)) }
        }
    }
}