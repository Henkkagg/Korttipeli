package com.example.korttipeli.presentation.main_menu.cards.card_inspector

import android.net.Uri

sealed class CardInspectorEvent {
    //1=action, 2=virus, 3=secret
    data class TypeChanged(val value: Int) : CardInspectorEvent()
    data class TitleTyped(val value: String) : CardInspectorEvent()
    data class DescriptionTyped(val value: String) : CardInspectorEvent()
    data class ImageChanged(val uri: Uri) : CardInspectorEvent()

    object ConfirmPressed : CardInspectorEvent()
    object PreviewPressed : CardInspectorEvent()
    object EditPressed : CardInspectorEvent()
    object DeletePressed : CardInspectorEvent()
    object CancelPressed : CardInspectorEvent()
    object AlertDialogConfirmed : CardInspectorEvent()
    object AlertDialogDismissed : CardInspectorEvent()
}

sealed class InspectorMode(var editorEnabled: Boolean) {
    object CreatingNew : InspectorMode(true)
    object PreviewingNew : InspectorMode(false)

    object InspectingOwn : InspectorMode(false)
    object EditingOwn : InspectorMode(true)
    object PreviewingChanges : InspectorMode(false)

    object InspectingFriends: InspectorMode(false)
    object DuringGame : InspectorMode(false)
    object UsingSecret : InspectorMode(false)
}