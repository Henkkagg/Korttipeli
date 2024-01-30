package com.example.korttipeli.presentation.main_menu.cards.deck_editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.domain.model.Deck
import com.example.korttipeli.domain.use_case.CardUsecases
import com.example.korttipeli.domain.use_case.DeckUsecases
import com.example.korttipeli.domain.use_case.IOUsecases
import com.example.korttipeli.domain.use_case.Result
import com.example.korttipeli.presentation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckEditorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val cardUsecases: CardUsecases,
    private val deckUsecases: DeckUsecases,
    private val ioUsecases: IOUsecases,
    private val sharedPref: SharedPref
) : ViewModel() {
    val navigateTo = savedStateHandle.getStateFlow(
        "navigateTo", NavigateTo.Nowhere as NavigateTo
    )
    val toastMessage = savedStateHandle.getStateFlow("toastMessage", "")
    val creationStage = savedStateHandle.getStateFlow(
        "creationStage", CreationStage.NameAndPicture as CreationStage
    )

    val name = savedStateHandle.getStateFlow("name", "")
    var bitmap: Bitmap? by mutableStateOf(null)
        private set

    //i = 0 Action, i = 1 Virus, i = 2 Secret
    val cardTypes = savedStateHandle.getStateFlow("cardTypes", intArrayOf(0, 0, 0))
    val cardIds = savedStateHandle.getStateFlow("cardIds", emptyList<String>())

    private lateinit var oldDeck: Deck
    private lateinit var oldBitmap: Bitmap

    init {
        savedStateHandle["creationStage"] = CreationStage.NameAndPicture as CreationStage
        initializeDeck()
    }

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.NameTyped -> savedStateHandle["name"] = event.value
            is UiEvent.ImageChanged -> bitmap = cardUsecases.createBitmapFromUri(event.uri)
            UiEvent.ChooseCardsPressed -> {
                savedStateHandle["creationStage"] = CreationStage.ChoosingCards
            }
            is UiEvent.SelectionChanged -> updateCardTypes(event.cardIds)
            UiEvent.ImagePressed -> savedStateHandle["navigateTo"] = NavigateTo.ImagePicker
            UiEvent.ConfirmPressed -> uploadDeck()
            UiEvent.CardSelectionCanceled -> {
                savedStateHandle["creationStage"] = CreationStage.NameAndPicture
            }
        }
    }

    private fun uploadDeck() = viewModelScope.launch {
        savedStateHandle["creationStage"] = CreationStage.Processing

        val result = if (savedStateHandle.navArgs<DeckEditorNavArgs>().deckId == "") {

            //Creating new
            deckUsecases.createNew(
                name.value,
                bitmap!!,
                cardIds.value,
                sharedPref.readUsername()
            )
        } else {
            //Updating
            deckUsecases.update(
                deckId = oldDeck.id,
                name = if (oldDeck.name != name.value || oldDeck.cardList != cardIds.value) {
                    name.value
                } else {
                    ""
                },
                bitmap = if (oldBitmap != bitmap) bitmap else null,
                cardIds = if (oldDeck.name != name.value || oldDeck.cardList != cardIds.value) {
                    cardIds.value
                } else {
                    emptyList()
                }
            )
        }

        if (result is Result.Success) {
            savedStateHandle["navigateTo"] = NavigateTo.Exit
            savedStateHandle["toastMessage"] = "Onnistui"
        }
        if (result is Result.Failure) {
            savedStateHandle["toastMessage"] = result.reason
            savedStateHandle["creationStage"] = CreationStage.ChoosingCards
        }
    }

    private fun updateCardTypes(cardIds: List<String>) {
        savedStateHandle["cardIds"] = cardIds.toList()
        viewModelScope.launch {
            savedStateHandle["cardTypes"] = deckUsecases.getTypesByIds(cardIds)
        }
    }

    fun updateUri(uri: Uri) {
        savedStateHandle["imageUri"] = uri
    }

    fun rogerNavigation() {
        savedStateHandle["navigateTo"] = NavigateTo.Nowhere
    }

    private fun initializeDeck() {
        val deckId = savedStateHandle.navArgs<DeckEditorNavArgs>().deckId
        if (deckId != "") viewModelScope.launch {
            oldDeck = deckUsecases.getOneById(deckId)!!

            savedStateHandle["name"] = oldDeck.name
            bitmap = ioUsecases.loadImage(oldDeck.image)
            oldBitmap = bitmap!!

            savedStateHandle["cardIds"] = oldDeck.cardList
            savedStateHandle["cardTypes"] = deckUsecases.getTypesByIds(oldDeck.cardList)
        }
    }
}