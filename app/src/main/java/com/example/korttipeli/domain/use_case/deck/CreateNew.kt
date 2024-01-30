package com.example.korttipeli.domain.use_case.deck

import android.graphics.Bitmap
import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.Deck
import com.example.korttipeli.domain.model.DeckToServer
import com.example.korttipeli.domain.model.Ids
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

class CreateNew @Inject constructor(
    private val repository: DecksRepository,
    private val db: Database,
    private val directory: File,
) {


    suspend operator fun invoke(
        name: String,
        bitmap: Bitmap,
        cardIds: List<String>,
        username: String
    ) = withContext(Dispatchers.IO) {

        val base64Image = ConvertBitmapToBase64()(bitmap)

        val data = Gson().toJson(
            DeckToServer(
                name = name,
                base64Image = base64Image,
                cardIds = cardIds
            )
        )
        val response = repository.createDeck(data)

        if (response is UnhandledResponse.Failure) {
            val reason = when (response.httpStatusCode) {
                HttpStatusCode.NotAcceptable -> "Nimi on liian pitkä"
                HttpStatusCode.LengthRequired -> "Pakassa on liikaa kortteja"
                HttpStatusCode.PayloadTooLarge -> "Kuvatiedosto on liian suuri"
                else -> "Palvelinvirhe"
            }
            return@withContext Result.Failure(reason)
        }

        val body = (response as UnhandledResponse.Success).body
        val ids = Gson().fromJson(body, Ids::class.java)

        val imageFile = File(directory, ids.idForImage)
        imageFile.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        //Bad practice to perform name cleanup client side. It's done server side same way too tho
        val deck = Deck(
            id = ids.id,
            idForImage = ids.idForImage,
            idForNonImage = ids.idForNonImage,
            image = imageFile.path,
            name = Util.cleanName(name),
            author = username,
            cardList = cardIds,
        )
        db.decksDao().insertOne(deck)

        return@withContext Result.Success
    }
}