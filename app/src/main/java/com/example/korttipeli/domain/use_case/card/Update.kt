package com.example.korttipeli.domain.use_case.card

import android.graphics.Bitmap
import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.CardInfo
import com.example.korttipeli.domain.model.UpdatedImage
import com.example.korttipeli.domain.model.UpdatedInfo
import com.example.korttipeli.domain.repository.CardsRepository
import com.example.korttipeli.domain.use_case.CardResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class Update @Inject constructor(
    private val repository: CardsRepository,
    private val db: Database,
    private val directory: File
) {

    suspend operator fun invoke(cardInfo: CardInfo, bitmap: Bitmap?): CardResult {
        return withContext(Dispatchers.IO) {
            val result = repository.updateExisting(cardInfo)
            if (result !is CardResult.Success) return@withContext result

            if (bitmap != null) {
                val imageFile = File(directory, result.idForImage)
                imageFile.outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                val updatedImage = UpdatedImage(result.id, result.idForImage, imageFile.toString())
                db.cardsDao().updateImages(listOf(updatedImage))
            }

            val updatedInfo = UpdatedInfo(
                result.id,
                result.idForOtherThanImage,
                cardInfo.title,
                cardInfo.description,
                cardInfo.type
            )
            db.cardsDao().updateInfos(listOf(updatedInfo))

            result
        }

    }
}