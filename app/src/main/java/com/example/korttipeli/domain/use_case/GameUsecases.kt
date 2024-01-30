package com.example.korttipeli.domain.use_case

import com.example.korttipeli.domain.use_case.game.CloseSession
import com.example.korttipeli.domain.use_case.game.FetchGames
import com.example.korttipeli.domain.use_case.game.GetGameStream
import com.example.korttipeli.domain.use_case.game.SendInput
import javax.inject.Inject

data class GameUsecases @Inject constructor(
    val getGameStream: GetGameStream,
    val sendInput: SendInput,
    val fetchGames: FetchGames,
    val closeSession: CloseSession
)