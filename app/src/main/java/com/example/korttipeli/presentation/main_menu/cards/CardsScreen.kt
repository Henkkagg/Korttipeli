package com.example.korttipeli.presentation.main_menu.cards

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.domain.model.CardUi
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.destinations.CardInspectorScreenDestination
import com.example.korttipeli.ui.theme.*
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    appState: AppState,
    selectionMode: Boolean = false,
    preselectedCards: List<String> = emptyList(),
    viewModel: CardsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit = {},
    onSelectionChanged: (List<String>) -> Unit = {}
) {
    val selectedCards = remember { mutableStateListOf(*preselectedCards.toTypedArray()) }
    var showSettings: Boolean by remember { mutableStateOf(false) }

    if (viewModel.shouldNavigateToCardEditor) {
        viewModel.acknowledgeNavigation()
        navigator.navigate(CardInspectorScreenDestination(viewModel.destinationId))
    }

    LaunchedEffect(Unit) {
        if (!selectionMode) {
            appState.showFab(Icons.Default.Add) { viewModel.onEvent(UiEvent.NewCardPressed) }
        }
        viewModel.onInit()
    }

    BackHandler {
        if (showSettings) showSettings = false else onBackPressed()
    }

    AnimatedVisibility(
        visible = showSettings,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        SortingSettings(viewModel = viewModel)
    }

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize(),
        columns = GridCells.Fixed(2)
    ) {
        val sorting = viewModel.sortingSetting
        var previousAuthor = ""
        val handledAuthors = hashSetOf<String>()

        viewModel.cards.forEach {

            //Very ugly, but actually efficient
            if ((handledAuthors.add(it.author) && (sorting.separateAuthors || sorting.showOwnOnTop))
                || (handledAuthors.size == 1 && previousAuthor != it.author)
            ) {

                val header = when {
                    !sorting.showOwnOnTop && !sorting.separateAuthors -> "Kaikki"
                    sorting.showOwnOnTop && previousAuthor == ""
                            && it.author == viewModel.username -> "Omat"
                    sorting.showOwnOnTop && !sorting.separateAuthors -> "Muiden"
                    else -> it.author
                }
                val isFirstHeader = handledAuthors.size == 1

                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = header,
                            style = MaterialTheme.typography.h1
                        )
                        if (isFirstHeader) {
                            IconButton(
                                onClick = { showSettings = !showSettings }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Järjestysasetukset"
                                )
                            }
                        }
                    }
                }
                previousAuthor = it.author
            }

            item {
                CardPreview(
                    cardUi = it,
                    selected = selectedCards.contains(it.id)
                ) { card ->
                    if (!selectionMode) {
                        navigator.navigate(CardInspectorScreenDestination(card.id))
                    } else {
                        if (selectedCards.contains(it.id)) {
                            selectedCards.remove(it.id)
                        } else selectedCards.add(it.id)
                        onSelectionChanged(selectedCards)
                    }
                }
            }
        }
    }

}

@Composable
fun CardPreview(
    cardUi: CardUi,
    selected: Boolean = false,
    onPressed: (CardUi) -> Unit = {}
) {
    var backgroundColor = remember { Grey }
    var borderColor = remember { BorderGrey }

    when (cardUi.type) {
        //Action
        1 -> {
            backgroundColor = Grey
            borderColor = BorderGrey
        }
        //Virus
        2 -> {
            backgroundColor = Green
            borderColor = BorderGreen
        }
        //Secret
        3 -> {
            backgroundColor = Pink
            borderColor = BorderPink
        }
    }
    Card(
        modifier = Modifier
            .border(MaterialTheme.spacing.border, borderColor, MaterialTheme.cardShapes.preview)
            .clip(MaterialTheme.cardShapes.preview)
            .aspectRatio(1.5f)
            .clickable { onPressed(cardUi) }
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Image(
                bitmap = cardUi.image.asImageBitmap(),
                contentDescription = cardUi.title,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.small)
                ) {
                    Text(
                        text = cardUi.title,
                        modifier = Modifier.offset(y = -MaterialTheme.spacing.border),
                        style = MaterialTheme.typography.subtitle1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (selected) Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ButtonBlue.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Valittu",
                    style = MaterialTheme.typography.h2
                )
            }
        }
    }
}
