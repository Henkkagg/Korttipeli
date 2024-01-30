package com.example.korttipeli.domain.use_case.deck

import android.graphics.Bitmap
import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.*
import com.example.korttipeli.domain.repository.DecksRepository
import com.example.korttipeli.domain.use_case.Result
import com.example.korttipeli.domain.use_case.UnhandledResponse
import com.example.korttipeli.domain.use_case.Util
import com.example.korttipeli.domain.use_case.card.ConvertBitmapToBase64
import com.google.gson.Gson
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class Update @Inject constructor(
    private val repository: DecksRepository,
    private val db: Database,
    private val directory: File,
    private val convertBitmapToBase64: ConvertBitmapToBase64
) {

    suspend operator fun invoke(
        deckId: String,
        name: String,
        bitmap: Bitmap?,
        cardIds: List<String>
    ) = withContext(Dispatchers.IO) {

        val base64Image = if (bitmap != null) convertBitmapToBase64(bitmap) else ""

        val deckToServer = DeckToServer(
            deckId = deckId,
            name = Util.cleanName(name),
            base64Image = base64Image,
            cardIds = cardIds
        )

        val data = Gson().toJson(deckToServer)
        val unhandledResponse = repository.updateDeck(data)

        if (unhandledResponse is UnhandledResponse.Failure) {
            val reason = when (unhandledResponse.httpStatusCode) {
                HttpStatusCode.NotAcceptable -> "Nimi on liian pitkä"
                HttpStatusCode.LengthRequired -> "Pakassa on liikaa kortteja"
                HttpStatusCode.PayloadTooLarge -> "Kuvatiedosto on liian suuri"
                else -> "Palvelinvirhe"
            }
            return@withContext Result.Failure(reason)
        }

        val body = (unhandledResponse as UnhandledResponse.Success).body
        val ids = Gson().fromJson(body, Ids::class.java)

        //Update image to room if it was changed
        if (deckToServer.base64Image.isNotEmpty()) {
            val imageFile = File(directory, ids.idForImage)
            imageFile.outputStream().use {
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }

            val updatedImage = UpdatedImage(
                id = ids.id,
                idForImage = ids.idForImage,
                image = imageFile.path
            )
            db.decksDao().updateImages(listOf(updatedImage))
        }

        //Update other details if anything was changed
        if (deckToServer.name.isNotEmpty() && deckToServer.cardIds.isNotEmpty()) {

            val deckUpdatedNonImage = DeckUpdatedNonImage(
                id = ids.id,
                idForNonImage = ids.idForNonImage,
                name = deckToServer.name,
                cardList = deckToServer.cardIds
            )
            db.decksDao().updateNonImages(listOf(deckUpdatedNonImage))
        }

        Result.Success as Result
    }

}