package com.example.korttipeli.domain.repository

import com.example.korttipeli.domain.model.Game
import com.example.korttipeli.domain.model.GameInput
import com.example.korttipeli.domain.model.NewGame
import kotlinx.coroutines.flow.Flow

interface GameRepository {

    suspend fun fetchGames(): List<Game>

    suspend fun getGameStream(gameId: String, newGame: NewGame?): Flow<Game>

    suspend fun sendInput(input: GameInput)

    suspend fun closeSession()

}