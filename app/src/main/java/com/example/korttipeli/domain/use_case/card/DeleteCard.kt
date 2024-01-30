package com.example.korttipeli.domain.use_case.card

import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.repository.CardsRepository
import com.example.korttipeli.domain.use_case.CardResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class DeleteCard @Inject constructor(
    private val repository: CardsRepository,
    private val db: Database,
    private val directory: File
) {

    suspend operator fun invoke(cardId: String): CardResult {
        return withContext(Dispatchers.IO) {
            val result = repository.deleteExisting(cardId)

            if (result !is CardResult.Success) return@withContext CardResult.ServerError

            runCatching {
                db.cardsDao().deleteByIds(listOf(cardId))
                CardResult.Success("")
            }.getOrDefault(CardResult.ClientError)
        }
    }
}