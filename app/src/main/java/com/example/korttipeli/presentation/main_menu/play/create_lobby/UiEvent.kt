package com.example.korttipeli.presentation.main_menu.play.create_lobby

import android.os.Parcelable
import com.example.korttipeli.domain.model.Deck
import kotlinx.parcelize.Parcelize

sealed class UiEvent {
    object SelectDeckPressed : UiEvent()
    object BackPressed : UiEvent()
    object ReadyPressed : UiEvent()
    data class NameTyped(val value: String) : UiEvent()
    data class DeckSelected(val deck: Deck) : UiEvent()
}

@Parcelize
sealed class NavigateTo: Parcelable {
    object Nowhere : NavigateTo()
    object Game : NavigateTo()
    object Back : NavigateTo()
}
