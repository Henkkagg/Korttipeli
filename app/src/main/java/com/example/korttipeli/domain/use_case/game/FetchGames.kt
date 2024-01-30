package com.example.korttipeli.domain.use_case.game

import com.example.korttipeli.domain.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchGames @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke() = withContext(Dispatchers.IO) {

        gameRepository.fetchGames()
    }
}