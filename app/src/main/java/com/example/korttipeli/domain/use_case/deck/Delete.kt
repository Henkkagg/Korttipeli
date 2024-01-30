package com.example.korttipeli.domain.use_case.deck

import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.repository.DecksRepository
import com.example.korttipeli.domain.use_case.Result
import com.example.korttipeli.domain.use_case.UnhandledResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Delete @Inject constructor(
    private val repository: DecksRepository,
    private val db: Database
) {

    suspend operator fun invoke(deckId: String) = withContext(Dispatchers.IO) {
        val unhandledResponse = repository.deleteDeck(deckId)
        if (unhandledResponse is UnhandledResponse.Failure) {
            return@withContext Result.Failure("Jotain meni vikaan")
        }

        val deletedId = (unhandledResponse as UnhandledResponse.Success).body
        db.decksDao().deleteByIds(listOf(deletedId))

        return@withContext Result.Success
    }
}