package com.example.korttipeli.presentation.main_menu.play.game

sealed class UiEvent {
    object BackPressed : UiEvent()
    object ConfirmExitPressed : UiEvent()
    object CancelExitPressed : UiEvent()
    object StartGamePressed : UiEvent()
    object FlipCardPressed : UiEvent()
    object CardPressed : UiEvent()
    object TurnDonePressed : UiEvent()
    object CheckSecretsPressed : UiEvent()
    object CheckVirusPressed : UiEvent()

    data class ConfirmSecretUsePressed(val cardId: String) : UiEvent()
}

sealed class GameState {
    object Loading : GameState()
    object InLobby : GameState()
    object WaitingReveal : GameState()
    object WaitingTurnEnd : GameState()
    object CheckingSecrets : GameState()
    object CheckingVirus : GameState()
    object GameOver : GameState()
}
