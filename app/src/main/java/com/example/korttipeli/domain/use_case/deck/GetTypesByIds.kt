package com.example.korttipeli.domain.use_case.deck

import com.example.korttipeli.data.room.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTypesByIds @Inject constructor(
    private val db: Database
) {

    suspend operator fun invoke(cardIds: List<String>): IntArray {
        return withContext(Dispatchers.IO) {
            val cardInfos = db.cardsDao().getInfosByIds(cardIds)

            var action = 0
            var virus = 0
            var secret = 0

            cardInfos.forEach {
                when (it.type) {
                    1 -> action++
                    2 -> virus++
                    3 -> secret++
                }
            }

            intArrayOf(action, virus, secret)
        }
    }
}