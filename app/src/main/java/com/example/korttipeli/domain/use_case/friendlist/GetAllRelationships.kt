package com.example.korttipeli.domain.use_case.friendlist

import com.example.korttipeli.domain.model.RelationshipStatuses
import com.example.korttipeli.domain.repository.FriendlistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAllRelationships @Inject constructor(private val repository: FriendlistRepository) {

    suspend operator fun invoke(): RelationshipStatuses {

        return withContext(Dispatchers.IO) {
            repository.getAllRelationships()
        }

    }
}