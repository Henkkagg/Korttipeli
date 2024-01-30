package com.example.korttipeli.domain.use_case.card

import android.util.Log
import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadOne @Inject constructor(private val db: Database) {

    suspend operator fun invoke(id: String): Card {
        return withContext(Dispatchers.IO) {

            Log.i("apu", listOf(id).first())
            db.cardsDao().getCardsByIds(listOf(id)).first()
        }
    }
}