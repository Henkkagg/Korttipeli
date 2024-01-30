package com.example.korttipeli.domain.repository

import com.example.korttipeli.domain.model.*
import com.example.korttipeli.domain.use_case.CardResult

interface CardsRepository {

    suspend fun getAllCards(cardIds: List<Ids>): CardDataPackage?

    suspend fun getSpecifigCard(id: String): Card

    suspend fun getAllDecks(): List<Deck>

    //Uploads the image to backend
    suspend fun preUploadImage(base64Image: String): CardResult

    suspend fun createNew(cardInfo: CardInfo): CardResult

    suspend fun updateExisting(cardInfo: CardInfo): CardResult

    suspend fun deleteExisting(cardId: String): CardResult

    suspend fun testRequest(base64Image: String): CardResult

}