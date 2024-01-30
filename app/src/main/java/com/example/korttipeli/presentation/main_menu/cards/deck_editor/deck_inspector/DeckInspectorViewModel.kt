package com.example.korttipeli.presentation.main_menu.cards.deck_editor.deck_inspector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.R
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.domain.model.Card
import com.example.korttipeli.domain.model.Deck
import com.example.korttipeli.domain.use_case.CardUsecases
import com.example.korttipeli.domain.use_case.DeckUsecases
import com.example.korttipeli.domain.use_case.IOUsecases
import com.example.korttipeli.domain.use_case.Result
import com.example.korttipeli.presentation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckInspectorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deckUsecases: DeckUsecases,
    private val cardUsecases: CardUsecases,
    private val ioUsecases: IOUsecases,
    private val sharedPref: SharedPref,
    @ApplicationContext context: Context
) : ViewModel() {

    var deck by mutableStateOf(Deck())
        private set
    var cards: List<Card> by mutableStateOf(emptyList())
        private set
    var bitmap: Bitmap by mutableStateOf(
        BitmapFactory.decodeResource(context.resources, R.drawable.loading)
    )
        private set
    var shouldBeEditable: Boolean by mutableStateOf(false)
        private set
    var shouldShowAlertDialog by mutableStateOf(false)
        private set
    var navigateTo: NavigateTo by mutableStateOf(NavigateTo.Nowhere)
        private set
    var toastMessage: String by mutableStateOf("")
        private set



    fun onInit() {
        viewModelScope.launch {
            val deckId = savedStateHandle.navArgs<DeckInspectorNavArgs>().id

            deck = deckUsecases.getOneById(deckId)!!
            deck.types = deckUsecases.getTypesByIds(deck.cardList)
            bitmap = ioUsecases.loadImage(deck.image)

            if (sharedPref.readUsername() == deck.author) shouldBeEditable = true
            loadAndSortCards()
        }
    }

    fun onEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            UiEvent.DeletePressed -> {
                shouldShowAlertDialog = true
            }
            UiEvent.EditPressed -> {
                navigateTo = NavigateTo.Editor(deck.id)
            }
            UiEvent.DeleteCanceled -> {
                shouldShowAlertDialog = false
            }
            UiEvent.DeleteConfirmed -> viewModelScope.launch {
                val result = deckUsecases.delete(deck.id)
                if (result is Result.Success) {
                    toastMessage = "Poistaminen onistui"
                    navigateTo = NavigateTo.Exit
                } else toastMessage = "Jotain meni vikaan"
                shouldShowAlertDialog = false
            }
        }

    }

    private fun loadAndSortCards() {
        viewModelScope.launch {
            val unsortedCards = cardUsecases.loadByIds(deck.cardList)
            cards = unsortedCards.sortedWith(compareBy({ it.type }, { it.title }))
        }
    }

    fun loadImage(fileLocation: String): Bitmap {
        return ioUsecases.loadImage(fileLocation)
    }

    fun rogerNavigation() {
        navigateTo = NavigateTo.Nowhere
    }
}