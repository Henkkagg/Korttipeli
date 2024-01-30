package com.example.korttipeli.presentation.main_menu.cards.deck_editor.deck_inspector


sealed class UiEvent {
    object EditPressed : UiEvent()
    object DeletePressed : UiEvent()

    object DeleteCanceled : UiEvent()
    object DeleteConfirmed : UiEvent()
}

sealed class NavigateTo {
    object Nowhere : NavigateTo()
    object Exit : NavigateTo()
    data class Editor(val deckId: String) : NavigateTo()
}