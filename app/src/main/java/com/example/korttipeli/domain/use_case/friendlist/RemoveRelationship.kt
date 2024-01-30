package com.example.korttipeli.domain.use_case.friendlist

import com.example.korttipeli.domain.repository.FriendlistRepository
import com.example.korttipeli.domain.use_case.FriendlistResult
import javax.inject.Inject

class RemoveRelationship @Inject constructor(private val repository: FriendlistRepository) {

    suspend operator fun invoke(username: String): FriendlistResult {

        return repository.removeRelationship(username)
    }
}