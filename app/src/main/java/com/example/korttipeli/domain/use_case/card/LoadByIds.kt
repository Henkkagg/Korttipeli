package com.example.korttipeli.domain.use_case.card

import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

//If ids aren't specified, load all
class LoadByIds @Inject constructor(private val db: Database) {

    suspend operator fun invoke(ids: List<String>? = null): List<Card> {
        return withContext(Dispatchers.IO) {

            if (ids == null) {
                db.cardsDao().getAll()
            } else {
                db.cardsDao().getCardsByIds(ids)
            }
        }
    }
}