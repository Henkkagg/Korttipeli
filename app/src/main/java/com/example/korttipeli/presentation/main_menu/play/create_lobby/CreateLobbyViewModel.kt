package com.example.korttipeli.presentation.main_menu.play.create_lobby

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.domain.model.Deck
import com.example.korttipeli.domain.use_case.GameUsecases
import com.example.korttipeli.domain.use_case.IOUsecases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateLobbyViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val gameUsecases: GameUsecases,
    private val ioUsecases: IOUsecases
) : ViewModel() {

    val navigateTo = savedStateHandle.getStateFlow("navigateTo", NavigateTo.Nowhere as NavigateTo)
    val toast = savedStateHandle.getStateFlow("toast", "")

    val name = savedStateHandle.getStateFlow("name", "")
    val conditionsMet = savedStateHandle.getStateFlow("conditionsMet", false)

    var deck: Deck? by mutableStateOf(null)
        private set
    var bitmap: Bitmap? by mutableStateOf(null)
        private set

    val inSelectionMode = savedStateHandle.getStateFlow("inSelectionMode", false)

    private var gameId: String? = null

    fun onEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            is UiEvent.NameTyped -> {
                savedStateHandle["name"] = uiEvent.value
            }
            UiEvent.SelectDeckPressed -> {
                savedStateHandle["inSelectionMode"] = true
            }
            is UiEvent.DeckSelected -> {
                deck = uiEvent.deck
                bitmap = ioUsecases.loadImage(deck!!.image)
                savedStateHandle["inSelectionMode"] = false
            }
            UiEvent.ReadyPressed -> viewModelScope.launch {
                if (deck == null) return@launch
                savedStateHandle["navigateTo"] = NavigateTo.Game
            }
            UiEvent.BackPressed -> {
                if (inSelectionMode.value) {
                    savedStateHandle["inSelectionMode"] = false
                } else {
                    savedStateHandle["navigateTo"] = NavigateTo.Back
                }
            }
        }
        checkIfConditionsMet()
    }

    fun rogerNavigation() {
        savedStateHandle["navigateTo"] = NavigateTo.Nowhere
    }

    private fun checkIfConditionsMet() {
        savedStateHandle["conditionsMet"] = name.value.isNotBlank() && deck != null
    }

    fun testSocket() {
        if (gameId == null) {
            Log.i("apu", "Miten tää on null????")
            return
        }
    }

}