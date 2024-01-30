package com.example.korttipeli.data.repository

import com.example.korttipeli.data.HttpClientImpl
import com.example.korttipeli.data.toUnhandledResponse
import com.example.korttipeli.domain.model.DecksDataPackage
import com.example.korttipeli.domain.model.Ids
import com.example.korttipeli.domain.repository.DecksRepository
import com.example.korttipeli.domain.use_case.UnhandledResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class DecksImpl @Inject constructor(
    httpClientImpl: HttpClientImpl
): DecksRepository  {
    private val client = httpClientImpl.authenticated

    override suspend fun createDeck(data: String): UnhandledResponse {

        return client.post("/decks/create") {
            setBody(data)
        }.toUnhandledResponse()
    }

    override suspend fun updateDeck(data: String): UnhandledResponse {

        return client.post("/decks/update") {
            setBody(data)
        }.toUnhandledResponse()
    }

    override suspend fun getUpdates(ids: List<Ids>): DecksDataPackage {

        return client.get("/decks/get") {
            contentType(ContentType.Application.Json)
            setBody(ids)
        }.body()
    }

    override suspend fun deleteDeck(id: String): UnhandledResponse {

        return client.post("/decks/delete") {
            setBody(id)
        }.toUnhandledResponse()
    }
}