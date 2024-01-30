package com.example.korttipeli.data.repository

import com.example.korttipeli.data.HttpClientImpl
import com.example.korttipeli.domain.model.RelationshipStatuses
import com.example.korttipeli.domain.repository.FriendlistRepository
import com.example.korttipeli.domain.use_case.FriendlistResult
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class FriendlistImpl @Inject constructor(httpClientImpl: HttpClientImpl) : FriendlistRepository {
    private val client = httpClientImpl.authenticated

    override suspend fun getAllRelationships(): RelationshipStatuses {
        val response = client.get("/friendlist")
        val relationshipStatuses = runCatching {
            response.body<RelationshipStatuses>()
        }.getOrDefault(RelationshipStatuses(emptyList(), emptyList(), emptyList()))

        return relationshipStatuses
    }

    override suspend fun addFriend(username: String): FriendlistResult {
        val response = client.post("/friendlist/add") {
            setBody(username)
        }
        return when (response.status) {
            HttpStatusCode.OK -> FriendlistResult.Success
            HttpStatusCode.Found -> FriendlistResult.AlreadyInRelationship
            HttpStatusCode.NotFound -> FriendlistResult.UserNotFound
            HttpStatusCode.BadRequest -> FriendlistResult.UserNotFound
            else -> FriendlistResult.ServerError
        }
    }

    override suspend fun removeRelationship(username: String): FriendlistResult {
        val response = client.post("/friendlist/remove") {
            setBody(username)
        }
        return if (response.status == HttpStatusCode.OK) {
            FriendlistResult.Success
        } else FriendlistResult.ServerError
    }
}