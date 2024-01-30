package com.example.korttipeli.domain.repository

import com.example.korttipeli.domain.model.RelationshipStatuses
import com.example.korttipeli.domain.use_case.FriendlistResult

interface FriendlistRepository {

    suspend fun getAllRelationships(): RelationshipStatuses

    suspend fun addFriend(username: String): FriendlistResult

    suspend fun removeRelationship(username: String): FriendlistResult
}