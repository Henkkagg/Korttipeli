package com.example.korttipeli.domain.use_case.game

import com.example.korttipeli.domain.model.GameInput
import com.example.korttipeli.domain.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendInput @Inject constructor(
    private val repository: GameRepository
) {

    suspend operator fun invoke(input: GameInput) = withContext(Dispatchers.IO) {
        repository.sendInput(input)
    }
}