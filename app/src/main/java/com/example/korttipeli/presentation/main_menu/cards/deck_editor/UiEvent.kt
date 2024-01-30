package com.example.korttipeli.presentation.main_menu.cards.deck_editor

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class UiEvent {
    object ImagePressed : UiEvent()
    object ChooseCardsPressed : UiEvent()
    object ConfirmPressed : UiEvent()
    object CardSelectionCanceled : UiEvent()

    data class NameTyped(val value: String) : UiEvent()
    data class ImageChanged(val uri: Uri) : UiEvent()
    data class SelectionChanged(val cardIds: List<String>) : UiEvent()
}

@Parcelize
sealed class NavigateTo : Parcelable {
    object Nowhere : NavigateTo()
    object ImagePicker : NavigateTo()
    object Exit : NavigateTo()
}

@Parcelize
sealed class CreationStage : Parcelable {
    object NameAndPicture : CreationStage()
    object ChoosingCards : CreationStage()
    object Processing : CreationStage()
}