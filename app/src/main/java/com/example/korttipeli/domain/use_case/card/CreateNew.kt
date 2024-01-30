package com.example.korttipeli.domain.use_case.card

import android.graphics.Bitmap
import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.Card
import com.example.korttipeli.domain.model.CardInfo
import com.example.korttipeli.domain.repository.CardsRepository
import com.example.korttipeli.domain.use_case.CardResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class CreateNew @Inject constructor(
    private val repository: CardsRepository,
    private val db: Database,
    private val directory: File
) {

    //Client receives id for pre-uploaded image from backend. Id is used to identify appropriate
    //image from backend's temporary storage in case of multiple pre-uploaded images
    suspend operator fun invoke(
        cardInfo: CardInfo,
        bitmap: Bitmap
    ): CardResult {

        return withContext(Dispatchers.IO) {
            val networkResult = repository.createNew(cardInfo)
            if (networkResult !is CardResult.Success) return@withContext networkResult

            val imageFile = File(directory, networkResult.idForImage)
            imageFile.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            val card = Card(
                id = networkResult.id,
                idForImage = networkResult.idForImage,
                idForNonImage = networkResult.idForOtherThanImage,
                image = imageFile.toString(),
                author = networkResult.author,
                title = cardInfo.title,
                description = cardInfo.description,
                type = cardInfo.type
            )

            return@withContext runCatching {
                db.cardsDao().insertOne(card)
                CardResult.Success("")
            }.getOrDefault(CardResult.ClientError)
        }
    }
}