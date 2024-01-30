package com.example.korttipeli.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Game(
    val id: String = "",
    val deckId: String = "",
    val name: String = "",
    val owner: String = "",
    //0 = in lobby, 1 = waiting user to take card, 2 = waiting user to end turn
    val state: Int = -1,
    val players: List<String> = emptyList(),
    val playerInTurn: String = "",
    val cardsIdsRemaining: List<String> = emptyList(),
    val virusInPlay: VirusInplay? = null,
    val secretsInPlay: List<SecretInPlay> = emptyList()
)

data class VirusInplay(
    val cardId: String,
    val victim: String
)

data class SecretInPlay(
    val cardId: String,
    val holder: String
)

@Parcelize
data class NewGame(
    val deckId: String,
    val name: String
) : Parcelable

sealed class GameInput(val inputString: String) {
    object StartGame : GameInput("startGame")
    object RevealCard : GameInput("revealCard")
    object EndTurn : GameInput("endTurn")
    class UseSecret(cardId: String) : GameInput(cardId)
}

