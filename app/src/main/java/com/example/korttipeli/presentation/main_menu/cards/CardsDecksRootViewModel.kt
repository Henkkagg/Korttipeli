package com.example.korttipeli.presentation.main_menu.cards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardsDecksRootViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    val cardsSelected = savedStateHandle.getStateFlow("cardsSelected", true)
    val decksSelected = savedStateHandle.getStateFlow("decksSelected", false)

    fun cardsPressed() {
        savedStateHandle["cardsSelected"] = true
        savedStateHandle["decksSelected"] = false
    }
    fun decksPressed() {
        savedStateHandle["decksSelected"] = true
        savedStateHandle["cardsSelected"] = false
    }
}