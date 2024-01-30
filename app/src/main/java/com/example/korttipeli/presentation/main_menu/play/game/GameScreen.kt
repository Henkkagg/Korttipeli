package com.example.korttipeli.presentation.main_menu.play.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.R
import com.example.korttipeli.domain.model.Game
import com.example.korttipeli.domain.model.NewGame
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.common_components.CustomAlertDialog
import com.example.korttipeli.presentation.common_components.CustomButton
import com.example.korttipeli.presentation.destinations.CardInspectorScreenDestination
import com.example.korttipeli.presentation.destinations.PlayScreenDestination
import com.example.korttipeli.presentation.main_menu.cards.CardPreview
import com.example.korttipeli.presentation.main_menu.cards.DeckPreview
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

data class GameScreenNavArgs(
    val gameId: String = "",
    val newGame: NewGame? = null
)

@Destination(
    navArgsDelegate = GameScreenNavArgs::class
)
@Composable
fun GameScreen(
    appState: AppState,
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<CardInspectorScreenDestination, String>,
    viewModel: GameViewModel = hiltViewModel()
) {

    BackHandler {
        viewModel.onEvent(UiEvent.BackPressed)
    }

    val game by viewModel.gameFlow.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let { appState.showSnackbar(it) }
    }
    LaunchedEffect(viewModel.exitGame) {
        if (viewModel.exitGame) {
            navigator.popBackStack()
            navigator.navigate(PlayScreenDestination)
            viewModel.rogerNavigation()
        }
    }

    if (viewModel.shouldShowAlertDialog) {
        CustomAlertDialog(
            message = viewModel.exitGameWarningText,
            onConfirmText = "Kyllä",
            onDismissText = "Peruuta",
            onDismiss = { viewModel.onEvent(UiEvent.CancelExitPressed) }) {
            viewModel.onEvent(UiEvent.ConfirmExitPressed)
        }
    }

    resultRecipient.onNavResult { navResult ->
        if (navResult is NavResult.Value) {
            viewModel.onEvent(UiEvent.ConfirmSecretUsePressed(navResult.value))
        }
    }

    when (viewModel.gameState) {
        GameState.Loading -> LoadingScreen()
        GameState.InLobby -> LobbyScreen(game, viewModel, isAdmin)
        GameState.CheckingSecrets -> SecretsScreen(game, viewModel, navigator)
        GameState.CheckingVirus -> TODO()
        GameState.GameOver -> GameOverScreen(navigator)
        else -> GameDefaultScreen(game, viewModel, navigator)
    }
}

@Composable
private fun GameOverScreen(
    navigator: DestinationsNavigator
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Peli ohi!",
            style = MaterialTheme.typography.h1
        )
        Image(painter = painterResource(id = R.drawable.gameover), contentDescription = "GG")
        CustomButton(enabled = true, text = "Takaisin päävalikkoon") {
            navigator.navigateUp()
        }
    }
}

@Composable
private fun SecretsScreen(
    game: Game,
    viewModel: GameViewModel,
    navigator: DestinationsNavigator,
) {
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Text(
                text = "Salaisuutesi",
                style = MaterialTheme.typography.h1
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        items(viewModel.ownSecrets) { card ->
            CardPreview(cardUi = card) {
                navigator.navigate(
                    CardInspectorScreenDestination(
                        id = card.id,
                        isInGame = true,
                        isUsingSecret = true,
                    )
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

}

@Composable
private fun GameDefaultScreen(
    game: Game,
    viewModel: GameViewModel,
    navigator: DestinationsNavigator
) {
    if (viewModel.revealedSecret == null) {

        Column {
            val cardSide: CardSide = remember(game) {
                if (viewModel.gameState == GameState.WaitingTurnEnd) {
                    CardSide.Front
                } else {
                    CardSide.Back()
                }
            }
            val cardRotation = animateFloatAsState(
                targetValue = cardSide.rotation,
                animationSpec = tween(1000)
            )

            Text(
                text = game.playerInTurn,
                style = MaterialTheme.typography.h1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column {
                Card(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationY = if (cardSide is CardSide.Front) cardRotation.value else 180f
                            cameraDistance = 10f * density
                        }
                ) {
                    if (cardRotation.value >= 90f || cardSide is CardSide.Back) {
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    rotationY = 180f
                                }) {
                            CardPreview(cardUi = viewModel.cardBack) {
                                if (cardSide is CardSide.Back) {
                                    viewModel.onEvent(UiEvent.FlipCardPressed)
                                }
                            }
                        }
                    } else {
                        CardPreview(
                            cardUi = viewModel.newestCard!!
                        ) {
                            if (viewModel.newestCard!!.type == 3 &&
                                !viewModel.ownSecrets.map { it.id }
                                    .contains(viewModel.newestCard!!.id)
                            ) {
                                return@CardPreview
                            }
                            navigator.navigate(
                                CardInspectorScreenDestination(
                                    viewModel.newestCard!!.id, true
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Viruksen uhri: ${viewModel.currentVirusVictim}"
                )
                CustomButton(
                    enabled = viewModel.currentVirus != null,
                    text = "Näytä virus",
                ) {
                    navigator.navigate(
                        CardInspectorScreenDestination(
                            id = viewModel.currentVirus!!.id,
                            isInGame = true
                        )
                    )
                }
            }
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize()
            ) {
                val playerIsInTurn by remember(viewModel.gameState) {
                    mutableStateOf(game.playerInTurn == viewModel.username)
                }

                val buttonText = remember(viewModel.gameState) {
                    if (!playerIsInTurn) {
                        "Ei ole sinun vuorosi"
                    } else {
                        if (viewModel.gameState == GameState.WaitingReveal) {
                            "Korttisi odottaa kääntämistä"
                        } else {
                            "Lopeta vuoro"
                        }
                    }
                }

                Column {
                    CustomButton(
                        enabled = viewModel.ownSecrets.isNotEmpty(),
                        text = "Käytä salaisuuksia"
                    ) {
                        viewModel.onEvent(UiEvent.CheckSecretsPressed)
                    }
                    CustomButton(
                        enabled = playerIsInTurn && viewModel.gameState == GameState.WaitingTurnEnd,
                        text = buttonText
                    ) {
                        viewModel.onEvent(UiEvent.TurnDonePressed)
                    }
                }
            }
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${viewModel.revealedSecret!!.holder} käytti salaisuuden",
                style = MaterialTheme.typography.h1
            )
            Spacer(modifier = Modifier.height(10.dp))
            CardPreview(cardUi = viewModel.revealedSecretCard!!) {
                navigator.navigate(
                    CardInspectorScreenDestination(
                        id = viewModel.revealedSecret!!.cardId,
                        isInGame = true
                    )
                )
            }
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize()
            ) {
                CustomButton(enabled = true, text = "OK") {
                    viewModel.rogerRevealedSecret()
                }
            }
        }
    }
}

@Composable
private fun LobbyScreen(
    game: Game,
    viewModel: GameViewModel,
    isAdmin: Boolean
) {
    Column {
        Text(
            text = "Aula",
            style = MaterialTheme.typography.h1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        DeckPreview(
            deck = viewModel.deck!!,
            image = viewModel.deckBitmap!!
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(
                text = "Pelin nimi: ",
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
            Text(game.name)
        }
        Row {
            Text(
                text = "Pelin luoja: ",
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
            Text(game.owner)
        }
        Text(
            text = "Pelaajat",
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
        game.players.forEach {
            Text(
                text = it,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isAdmin) {
                CustomButton(
                    //game.players.size >= 2
                    enabled = true,
                    text = "Aloita peli"
                ) {
                    viewModel.onEvent(UiEvent.StartGamePressed)
                }
            } else {
                Text("Odotetaan että ${game.owner} aloittaa pelin")
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Ladataan",
            style = MaterialTheme.typography.h1
        )
    }
}

private sealed class CardSide(val rotation: Float) {
    object Front : CardSide(0f)
    data class Back(val flip: CardSide = Front) : CardSide(180f)
}