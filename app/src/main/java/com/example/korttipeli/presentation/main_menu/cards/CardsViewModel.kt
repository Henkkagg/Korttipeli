package com.example.korttipeli.presentation.main_menu.cards

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.domain.model.CardUi
import com.example.korttipeli.domain.use_case.CardUsecases
import com.example.korttipeli.domain.use_case.IOUsecases
import com.example.korttipeli.domain.use_case.card.SortingSetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cardsUsecases: CardUsecases,
    private val ioUsecases: IOUsecases,
    private val sharedPref: SharedPref
) : ViewModel() {

    var showCards: Boolean by mutableStateOf(true)
        private set
    var showDecks: Boolean by mutableStateOf(false)
        private set
    var sortingSetting: SortingSetting by mutableStateOf(SortingSetting())
        private set
    var cards: List<CardUi> by mutableStateOf(emptyList())
    var username = ""

    var shouldNavigateToCardEditor: Boolean by mutableStateOf(false)
        private set
    var shouldNavigateToDeckEditor: Boolean by mutableStateOf(false)
        private set
    var destinationId: String = ""
        private set

    init {
        username = sharedPref.readUsername()
        sortingSetting = sharedPref.readSortingSettings()
    }

    fun onInit() {
        viewModelScope.launch {
            val cachedCards = cardsUsecases.loadByIds()
            cards = cachedCards.map {
                CardUi(
                    id = it.id,
                    author = it.author,
                    title = it.title,
                    description = it.description,
                    image = ioUsecases.loadImage(it.image),
                    type = it.type
                )
            }
            cards = cardsUsecases.sortBySetting(cards, sortingSetting, username)

            cardsUsecases.getUpdates()
            val upToDateCards = cardsUsecases.loadByIds()

            if (cachedCards != upToDateCards) {
                cards = upToDateCards.map {
                    CardUi(
                        id = it.id,
                        author = it.author,
                        title = it.title,
                        description = it.description,
                        image = ioUsecases.loadImage(it.image),
                        type = it.type
                    )
                }
                cards = cardsUsecases.sortBySetting(cards, sortingSetting, username)
            }
        }
    }


    fun onEvent(event: UiEvent) {
        when (event) {
            UiEvent.NewCardPressed -> {
                destinationId = ""
                if (showCards) shouldNavigateToCardEditor = true
                if (showDecks) shouldNavigateToDeckEditor = true
            }
            UiEvent.ShowCardsPressed -> {
                showDecks = false
                showCards = true
            }
            UiEvent.ShowDecksPressed -> {
                showCards = false
                showDecks = true
            }
            UiEvent.SortAscendingToggled -> {
                sortingSetting = sortingSetting.copy(ascending = !sortingSetting.ascending)
                sortAndSave()
            }
            UiEvent.AuthorsSeparatedToggled -> {
                sortingSetting = sortingSetting.copy(separateAuthors =!sortingSetting.separateAuthors)
                sortAndSave()
            }
            UiEvent.OwnOnTopToggled -> {
                sortingSetting = sortingSetting.copy(showOwnOnTop =!sortingSetting.showOwnOnTop)
                sortAndSave()
            }
            UiEvent.TypesSeparatedToggled -> {
                sortingSetting = sortingSetting.copy(separateTypes =!sortingSetting.separateTypes)
                sortAndSave()
            }
        }

    }

    private fun sortAndSave() {
        cards = cardsUsecases.sortBySetting(cards, sortingSetting, username)
        Log.i("apu", cards.toString())
        sharedPref.saveSortingSettings(sortingSetting)
    }

    fun acknowledgeNavigation() {
        shouldNavigateToCardEditor = false
        shouldNavigateToDeckEditor = false
    }
}