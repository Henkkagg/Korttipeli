package com.example.korttipeli.domain.use_case

import com.example.korttipeli.domain.use_case.deck.*
import io.ktor.http.*
import javax.inject.Inject

data class DeckUsecases @Inject constructor(
    val getTypesByIds: GetTypesByIds,
    val createNew: CreateNew,
    val loadAndUpdate: LoadAndUpdate,
    val getOneById: GetOneById,
    val update: Update,
    val delete: Delete,
)

//Should have used general result sealed class instead of separate card result. Handling of said
//result should be the responsibility of use_case
sealed class Result {
    object Success : Result()
    data class Failure(val reason: String) : Result()
}

sealed class UnhandledResponse {
    data class Success(val body: String) : UnhandledResponse()
    data class Failure(val httpStatusCode: HttpStatusCode) : UnhandledResponse()
}