package com.example.korttipeli.data.repository

import android.util.Log
import com.example.korttipeli.data.HttpClientImpl
import com.example.korttipeli.domain.model.*
import com.example.korttipeli.domain.repository.CardsRepository
import com.example.korttipeli.domain.use_case.CardResult
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class CardsImpl @Inject constructor(
    httpClientImpl: HttpClientImpl
) : CardsRepository {
    private val client = httpClientImpl.authenticated

    override suspend fun getAllCards(ids: List<Ids>): CardDataPackage? {

        val response = client.get("/cards/get") {
            contentType(ContentType.Application.Json)
            setBody(ids)
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                response.body<CardDataPackage>()
            }
            else -> {
                Log.i("apu", "Ei saatu mitää korttei")
                null
            }
        }
    }

    override suspend fun getSpecifigCard(id: String): Card {
        TODO("Not yet implemented")
    }

    override suspend fun getAllDecks(): List<Deck> {
        TODO("Not yet implemented")
    }

    override suspend fun preUploadImage(base64Image: String): CardResult {
        val response = client.post("/cards/uploadimage") {
            setBody(base64Image)
        }

        return when (response.status) {
            HttpStatusCode.OK -> CardResult.Success(response.body())
            HttpStatusCode.NotAcceptable -> {
                val map = response.body<Map<String, Int>>()
                val (attemptedSizeInKb, allowedSizeInKb) = map.values.toList()
                CardResult.ImageTooLarge(attemptedSizeInKb, allowedSizeInKb)
            }
            else -> CardResult.ServerError
        }
    }

    override suspend fun createNew(cardInfo: CardInfo): CardResult {
        val response = client.post("/cards/create") {
            contentType(ContentType.Application.Json)
            setBody(cardInfo)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val infoList = response.body<Map<String, String>>().values.toList()
                if (infoList.size != 4) return CardResult.ServerError

                return CardResult.Success(infoList[0], infoList[1], infoList[2], infoList[3])
            }
            HttpStatusCode.PayloadTooLarge -> {
                val infoList = response.body<Map<String, Int>>().values.toList()
                if (infoList.size != 2) return CardResult.ServerError

                return CardResult.DescriptionTooLong(infoList[0], infoList[1])
            }
            HttpStatusCode.UnprocessableEntity -> {
                val infoList = response.body<Map<String, Int>>().values.toList()
                if (infoList.size != 2) return CardResult.ServerError

                return CardResult.TitleTooLong(infoList[0], infoList[1])
            }
            HttpStatusCode.MultiStatus -> {
                val infoList = response.body<Map<String, Int>>().values.toList()
                if (infoList.size != 4) return CardResult.ServerError

                return CardResult.TitleAndDescriptionTooLong(
                    infoList[0], infoList[1], infoList[2], infoList[3]
                )
            }
            else -> {
                return CardResult.ServerError
            }
        }
    }

    override suspend fun updateExisting(cardInfo: CardInfo): CardResult {
        val response = client.post("/cards/update") {
            contentType(ContentType.Application.Json)
            setBody(cardInfo)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val infoList = response.body<Map<String, String>>().values.toList()
                if (infoList.size != 3) return CardResult.ServerError

                return CardResult.Success(infoList[0], infoList[1], infoList[2])
            }
            HttpStatusCode.PayloadTooLarge -> {
                val infoList = response.body<Map<String, Int>>().values.toList()
                if (infoList.size != 2) return CardResult.ServerError

                return CardResult.DescriptionTooLong(infoList[0], infoList[1])
            }
            HttpStatusCode.UnprocessableEntity -> {
                val infoList = response.body<Map<String, Int>>().values.toList()
                if (infoList.size != 2) return CardResult.ServerError

                return CardResult.TitleTooLong(infoList[0], infoList[1])
            }
            HttpStatusCode.MultiStatus -> {
                val infoList = response.body<Map<String, Int>>().values.toList()
                if (infoList.size != 4) return CardResult.ServerError

                return CardResult.TitleAndDescriptionTooLong(
                    infoList[0], infoList[1], infoList[2], infoList[3]
                )
            }
            else -> {
                return CardResult.ServerError
            }
        }

    }

    override suspend fun deleteExisting(cardId: String): CardResult {
        val result = client.post("/cards/delete") {
            setBody(cardId)
        }

        when (result.status) {
            HttpStatusCode.OK -> return CardResult.Success("")
            else -> return CardResult.ServerError
        }
    }

    override suspend fun testRequest(base64Image: String): CardResult {
        client.post("/cards/test")

        return CardResult.ServerError
    }
}