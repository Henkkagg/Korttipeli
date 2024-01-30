package com.example.korttipeli.domain.use_case

import com.example.korttipeli.domain.use_case.friendlist.AddFriend
import com.example.korttipeli.domain.use_case.friendlist.GetAllRelationships
import com.example.korttipeli.domain.use_case.friendlist.RemoveRelationship
import javax.inject.Inject

data class FriendlistUsecases @Inject constructor(
    val addFriend: AddFriend,
    val getAllRelationships: GetAllRelationships,
    val removeRelationship: RemoveRelationship
)

sealed class FriendlistResult() {
    object Success : FriendlistResult()
    object AlreadyInRelationship : FriendlistResult()
    object UserNotFound : FriendlistResult()
    object ServerError : FriendlistResult()
}