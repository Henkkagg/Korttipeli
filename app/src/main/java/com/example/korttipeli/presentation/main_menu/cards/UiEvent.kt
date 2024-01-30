package com.example.korttipeli.presentation.main_menu.cards

sealed class UiEvent {

    //Remove these??
    object NewCardPressed : UiEvent()
    object ShowCardsPressed : UiEvent()
    object ShowDecksPressed : UiEvent()

    object SortAscendingToggled : UiEvent()
    object OwnOnTopToggled : UiEvent()
    object TypesSeparatedToggled : UiEvent()
    object AuthorsSeparatedToggled : UiEvent()


}