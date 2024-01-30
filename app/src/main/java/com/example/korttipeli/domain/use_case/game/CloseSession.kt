package com.example.korttipeli.domain.use_case.game

import com.example.korttipeli.domain.repository.GameRepository
import javax.inject.Inject

class CloseSession @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke() {

        gameRepository.closeSession()
    }
}