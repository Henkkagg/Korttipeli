package com.example.korttipeli.presentation.main_menu.play.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.R
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.domain.model.*
import com.example.korttipeli.domain.use_case.CardUsecases
import com.example.korttipeli.domain.use_case.DeckUsecases
import com.example.korttipeli.domain.use_case.GameUsecases
import com.example.korttipeli.domain.use_case.IOUsecases
import com.example.korttipeli.presentation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val gameUsecases: GameUsecases,
    private val deckUsecases: DeckUsecases,
    private val cardUsecases: CardUsecases,
    private val ioUsecases: IOUsecases,
    private val sharedPref: SharedPref,
    @ApplicationContext context: Context
) : ViewModel() {

    private val navArgs = savedStateHandle.navArgs<GameScreenNavArgs>()

    private val _gameFlow = MutableStateFlow(Game())
    val gameFlow = _gameFlow.asStateFlow()

    var gameState: GameState by mutableStateOf(GameState.Loading)
        private set

    val username = sharedPref.readUsername()

    var deck: Deck? by mutableStateOf(null)
        private set
    var deckBitmap: Bitmap? by mutableStateOf(null)
        private set
    var cards: List<CardUi> by mutableStateOf(emptyList())
        private set
    val cardBack = CardUi(
        id = "",
        title = "Paina paljastaaksesi kortin",
        image = BitmapFactory.decodeResource(context.resources, R.drawable.card_back),
        type = 1
    )
    var newestCard: CardUi? by mutableStateOf(null)
        private set

    var ownSecrets: List<CardUi> by mutableStateOf(emptyList())
        private set

    private val unrevealedSecretBitmap = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.secret_placeholder
    )
    var revealedSecret: SecretInPlay? by mutableStateOf(null)
        private set
    var revealedSecretCard: CardUi? = null

    var currentVirus: CardUi? by mutableStateOf(null)
        private set
    var currentVirusVictim: String by mutableStateOf("-")
        private set

    var shouldShowAlertDialog: Boolean by mutableStateOf(false)
        private set
    var toastMessage: String? by mutableStateOf(null)
        private set
    var exitGameWarningText = "Käynnissä olevaan peliin ei voi liittyä takaisin"
        private set

    var exitGame: Boolean by mutableStateOf(false)
        private set

    val isAdmin = savedStateHandle.getStateFlow("isAdmin", false)
    val isInTurn = savedStateHandle.getStateFlow("isInTurn", false)


    init {
        if (navArgs.gameId == "") {
            savedStateHandle["isAdmin"] = true
            exitGameWarningText = "Koska loit pelin, niin poistuessasi peli loppuu kaikilta"
        }

        viewModelScope.launch {
            gameUsecases.getGameStream(navArgs.gameId, navArgs.newGame).collect {

                if (it.state != gameFlow.value.state) {
                    handleStateChange(it)
                }
                if (it.cardsIdsRemaining.firstOrNull() != gameFlow.value.cardsIdsRemaining.firstOrNull()) {
                    handleNewCard(it)
                }
                if (it.secretsInPlay.size < gameFlow.value.secretsInPlay.size) {
                    handleRevealedSecret(it)
                }
                _gameFlow.value = it

                Log.i("apu", "${it.cardsIdsRemaining}")
            }
        }
    }

    private suspend fun handleNewCard(game: Game) {
        val userIsInTurn = game.playerInTurn == username
        val newCard = cardUsecases.loadOne(game.cardsIdsRemaining.first())

        Log.i("apu", "päästii tänne")

        //Don't reveal a secret if it's not our turn
        newestCard = if (newCard.type == 3 && !userIsInTurn) {
            CardUi(
                id = newCard.id,
                author = "?",
                title = "Paljastamaton salaisuus",
                description = "Salaisuuden haltija ei ole vielä paljastanut sitä",
                image = unrevealedSecretBitmap,
                type = 3
            )
        } else {
            cards.find { it.id == newCard.id }!!
        }

        if (newCard.type == 3 && userIsInTurn && game.cardsIdsRemaining.size <= cards.size) {
            ownSecrets = ownSecrets + CardUi(
                id = newCard.id,
                author = newCard.author,
                title = newCard.title,
                description = newCard.description,
                image = ioUsecases.loadImage(newCard.image),
                type = newCard.type
            )
        }

        if (newCard.type == 2) {
            currentVirus = newestCard
            currentVirusVictim = game.playerInTurn
        }
    }

    private suspend fun handleStateChange(game: Game) {
        if (deck == null || deckBitmap == null) {
            loadDeckAndCheckIntegrity(game.deckId, game)
        }
        gameState = when (game.state) {
            0 -> GameState.InLobby
            1 -> GameState.WaitingReveal
            2 -> GameState.WaitingTurnEnd
            else -> GameState.GameOver
        }
    }

    private suspend fun handleRevealedSecret(game: Game) {
        val secret = gameFlow.value.secretsInPlay.first {
            !game.secretsInPlay.contains(it)
        }

        val secretFullCard = cardUsecases.loadOne(secret.cardId)

        revealedSecretCard = CardUi(
            id = secretFullCard.id,
            author = secretFullCard.author,
            title = secretFullCard.title,
            description = secretFullCard.description,
            image = ioUsecases.loadImage(secretFullCard.image),
            type = secretFullCard.type
        )

        revealedSecret = secret
    }

    fun rogerRevealedSecret() {
        ownSecrets = ownSecrets.filterNot { it.id == revealedSecret!!.cardId }

        revealedSecret = null
    }

    private suspend fun loadDeckAndCheckIntegrity(deckId: String, game: Game) {
        cardUsecases.getUpdates()
        deckUsecases.loadAndUpdate().collect()

        deck = deckUsecases.getOneById(deckId)

        /*
        //User has no right to use the deck OR host is using outdated version of the deck
        if (deck == null || game.cardsIdsRemaining.sorted() != deck!!.cardList.sorted()) {
            toastMessage = "Virhe liittyessä. Pyydä hostia luomaan peli uudelleen"
            exitGame = true
            return
        }
         */

        deck!!.types = deckUsecases.getTypesByIds(deck!!.cardList)
        deckBitmap = ioUsecases.loadImage(deck!!.image)

        val fullCards = cardUsecases.loadByIds(deck!!.cardList)
        cards = fullCards.map {
            CardUi(
                id = it.id,
                author = it.author,
                title = it.title,
                description = it.description,
                image = ioUsecases.loadImage(it.image),
                type = it.type
            )
        }

        gameState = GameState.InLobby
    }

    fun rogerNavigation() {
        viewModelScope.launch {
            gameUsecases.closeSession()
        }
        exitGame = false
    }

    fun onEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            UiEvent.BackPressed -> {
                if (revealedSecret != null) {
                    rogerRevealedSecret()
                    return
                }

                if (gameState == GameState.CheckingSecrets || gameState == GameState.CheckingVirus) {
                    gameState = if (gameFlow.value.state == 1) {
                        GameState.WaitingReveal
                    } else {
                        GameState.WaitingTurnEnd
                    }
                } else {
                    shouldShowAlertDialog = true
                }
            }
            UiEvent.CancelExitPressed -> shouldShowAlertDialog = false
            UiEvent.ConfirmExitPressed -> exitGame = true
            UiEvent.StartGamePressed -> {
                viewModelScope.launch {
                    gameUsecases.sendInput(GameInput.StartGame)
                }
            }
            UiEvent.FlipCardPressed -> {
                if (gameFlow.value.playerInTurn != username) return
                viewModelScope.launch {
                    gameUsecases.sendInput(GameInput.RevealCard)
                    Log.i("apu", "Yritetää flipata")
                }
            }
            UiEvent.CardPressed -> Unit //Handled in composable
            UiEvent.TurnDonePressed -> {
                viewModelScope.launch {
                    gameUsecases.sendInput(GameInput.EndTurn)
                }
            }
            UiEvent.CheckVirusPressed -> TODO()
            UiEvent.CheckSecretsPressed -> gameState = GameState.CheckingSecrets

            is UiEvent.ConfirmSecretUsePressed -> {
                viewModelScope.launch {
                    handleStateChange(gameFlow.value)
                    gameUsecases.sendInput(GameInput.UseSecret(uiEvent.cardId))
                }
            }
        }
    }
}