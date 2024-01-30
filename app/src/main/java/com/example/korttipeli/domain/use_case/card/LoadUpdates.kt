package com.example.korttipeli.domain.use_case.card

import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.Card
import com.example.korttipeli.domain.model.CardDataPackageIds
import com.example.korttipeli.domain.model.UpdatedImage
import com.example.korttipeli.domain.model.UpdatedInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadUpdates @Inject constructor(private val db: Database) {

    suspend operator fun invoke(
        cardDataPackageIds: CardDataPackageIds
    ): Triple<List<Card>, List<UpdatedImage>, List<UpdatedInfo>> {
        return withContext(Dispatchers.IO) {
            val newCardsList = db.cardsDao().getCardsByIds(cardDataPackageIds.newCards)
            val updatedImagesList = db.cardsDao().getImagesByIds(cardDataPackageIds.updatedImages)
            val updatedInfosList = db.cardsDao().getInfosByIds(cardDataPackageIds.updatedInfos)

            Triple(newCardsList, updatedImagesList, updatedInfosList)
        }

    }
}