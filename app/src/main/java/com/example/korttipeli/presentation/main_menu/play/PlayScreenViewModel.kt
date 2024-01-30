package com.example.korttipeli.presentation.main_menu.play

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.domain.model.Game
import com.example.korttipeli.domain.use_case.GameUsecases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayScreenViewModel @Inject constructor(
    private val gameUsecases: GameUsecases,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    var navigateTo: NavigateTo by mutableStateOf(NavigateTo.Nowhere)
        private set

    var games: List<Game> by mutableStateOf(emptyList())
        private set

    fun onInit() = viewModelScope.launch {
        games = gameUsecases.fetchGames()

        Log.i("apu", "games")
    }

    fun onEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            UiEvent.CreatePressed -> navigateTo = NavigateTo.CreateNew
            is UiEvent.JoinPressed -> navigateTo = NavigateTo.Existing(uiEvent.game.id)
        }
    }

    fun rogerNavigation() {
        navigateTo = NavigateTo.Nowhere
    }

}