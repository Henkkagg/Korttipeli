package com.example.korttipeli.domain.use_case.game

import com.example.korttipeli.domain.model.Game
import com.example.korttipeli.domain.model.NewGame
import com.example.korttipeli.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGameStream @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(gameId: String, newGame: NewGame? = null): Flow<Game> {

        return repository.getGameStream(gameId, newGame)
    }
}