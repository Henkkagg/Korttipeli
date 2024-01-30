package com.example.korttipeli.presentation.main_menu.play

import com.example.korttipeli.domain.model.Game

sealed class UiEvent {
    object CreatePressed : UiEvent()
    data class JoinPressed(val game: Game) : UiEvent()
}

sealed class NavigateTo {
    object Nowhere : NavigateTo()
    object CreateNew : NavigateTo()
    data class Existing(val gameId: String) : NavigateTo()
}