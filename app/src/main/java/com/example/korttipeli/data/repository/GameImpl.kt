package com.example.korttipeli.data.repository

import android.util.Log
import com.example.korttipeli.data.HttpClientImpl
import com.example.korttipeli.data.websocket
import com.example.korttipeli.domain.model.Game
import com.example.korttipeli.domain.model.GameInput
import com.example.korttipeli.domain.model.NewGame
import com.example.korttipeli.domain.repository.GameRepository
import com.google.gson.Gson
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GameImpl @Inject constructor(
    httpClientImpl: HttpClientImpl
) : GameRepository {
    private val client = httpClientImpl.authenticated
    private var session: DefaultClientWebSocketSession? = null

    override suspend fun fetchGames(): List<Game> {

        return client.get("/game/fetch").body()
    }

    override suspend fun getGameStream(gameId: String, newGame: NewGame?): Flow<Game> {

        Log.i("apu", "Liitytään peliin $gameId")

        if (session != null) session!!.close()

        session = client.webSocketSession("/game/join/-$gameId") {
            websocket()
            if (newGame != null) {
                parameter("name", newGame.name)
                parameter("deckId", newGame.deckId)
            }
        }

        return session!!.incoming
            .receiveAsFlow()
            .filterIsInstance<Frame.Text>()
            .mapNotNull { Gson().fromJson(it.readText(), Game::class.java) }
    }

    override suspend fun sendInput(input: GameInput) {

        session?.send(input.inputString)
    }

    override suspend fun closeSession() {
        session?.close()
    }
}