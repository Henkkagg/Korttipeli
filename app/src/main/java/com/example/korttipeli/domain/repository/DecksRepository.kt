package com.example.korttipeli.domain.repository

import com.example.korttipeli.domain.model.DecksDataPackage
import com.example.korttipeli.domain.model.Ids
import com.example.korttipeli.domain.use_case.UnhandledResponse

interface DecksRepository {

    //Was trying different things. Hated making multiple versions of objects and so I made the
    //conversion to json in usecase. But in the end I think it just makes it more complicated
    //so back to using different models for different calls
    suspend fun createDeck(data: String): UnhandledResponse

    suspend fun updateDeck(data: String): UnhandledResponse

    suspend fun getUpdates(ids: List<Ids>): DecksDataPackage

    suspend fun deleteDeck(id: String): UnhandledResponse
}