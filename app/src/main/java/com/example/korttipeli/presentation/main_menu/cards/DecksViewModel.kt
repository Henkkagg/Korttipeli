package com.example.korttipeli.presentation.main_menu.cards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.domain.model.Deck
import com.example.korttipeli.domain.use_case.CardUsecases
import com.example.korttipeli.domain.use_case.DeckUsecases
import com.example.korttipeli.domain.use_case.IOUsecases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DecksViewModel @Inject constructor(
    private val deckUsecases: DeckUsecases,
    private val cardUsecases: CardUsecases,
    private val ioUsecases: IOUsecases,
    sharedPref: SharedPref,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _decks = MutableStateFlow(emptyList<Deck>())
    val decks = _decks.asStateFlow()

    private val username = sharedPref.readUsername()

    fun onInit() {
        viewModelScope.launch {
            cardUsecases.getUpdates()

            deckUsecases.loadAndUpdate().collect { decksList ->

                decksList.forEach {
                    it.types = deckUsecases.getTypesByIds(it.cardList)
                }

                //Simply put own decks on top. Everything sorted to ascending alphabetical order
                val ownDecks = decksList.filter {
                    it.author == username
                }.sortedBy { it.name }

                val othersDecks = decksList.filter {
                    it.author != username
                }.sortedBy { it.name }

                _decks.value = ownDecks + othersDecks
            }
        }

    }

    fun headerOrNull(deckIndex: Int): String? {
        val currentAuthor = decks.value[deckIndex].author
        val isOwn = currentAuthor == username

        if (deckIndex == 0 && isOwn) return "Omat"
        if (deckIndex == 0 && !isOwn) return "Muiden"

        if (!isOwn && decks.value[deckIndex - 1].author == username) return "Muiden"

        return null
    }

    fun loadImage(deck: Deck) = ioUsecases.loadImage(deck.image)
}