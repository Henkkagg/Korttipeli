package com.example.korttipeli.presentation.main_menu.friends

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.domain.model.RelationshipStatuses
import com.example.korttipeli.domain.use_case.FriendlistResult
import com.example.korttipeli.domain.use_case.FriendlistUsecases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendlistUsecases: FriendlistUsecases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var relationshipList by mutableStateOf(
        RelationshipStatuses(emptyList(), emptyList(), emptyList())
    )
        private set
    var isRefreshing: Boolean by mutableStateOf(false)
        private set
    var editModeEnabled: Boolean by mutableStateOf(false)
        private set
    var shouldShowRequests: Boolean by mutableStateOf(false)
        private set
    var usernameContent: String by mutableStateOf("")
        private set
    var resultMessage: String by mutableStateOf("")
        private set
    var resultJob: Job = viewModelScope.launch {  }
        private set
    var shouldShowAlertDialog: Boolean by mutableStateOf(false)
        private set

    fun refreshFriendlist(shouldShowRefreshIndicator: Boolean) {
        if (shouldShowRefreshIndicator) isRefreshing = true
        viewModelScope.launch {
            relationshipList = friendlistUsecases.getAllRelationships()
            isRefreshing = false
        }
    }

    fun onEvent(event: FriendsEvent) {
        when (event) {
            is FriendsEvent.AddFriendPressed -> {
                if (resultJob.isActive) {
                    Log.i("apu", "Lopeta rämppäys")
                    return
                }

                usernameContent = ""

                resultJob = viewModelScope.launch {
                    val result = friendlistUsecases.addFriend(event.username)
                    performActionBasedOnResult(result, event.relationshipState)
                }
            }
            is FriendsEvent.ConfirmRemovePressed -> {
                if (resultJob.isActive) return

                resultJob = viewModelScope.launch {
                    val result = friendlistUsecases.removeRelationship(event.username)
                    performActionBasedOnResult(result, event.relationshipState)
                }
            }
            is FriendsEvent.UsernameTyped -> {
                usernameContent = event.value
            }
            FriendsEvent.EditToggled -> {
                editModeEnabled = !editModeEnabled
            }
            FriendsEvent.RequestsToggled -> {
                shouldShowRequests = !shouldShowRequests
            }
            FriendsEvent.AlertdialogToggled -> {
                shouldShowAlertDialog = !shouldShowAlertDialog
            }
        }
    }

    private fun performActionBasedOnResult(result: FriendlistResult, relationshipState: Int) {
        when (result) {
            FriendlistResult.AlreadyInRelationship -> {
                resultMessage = "Käyttäjä on jo kaverilistallasi"
            }
            FriendlistResult.ServerError -> {
                resultMessage = "Jotain meni pieleen. Yritä uudelleen"
            }
            FriendlistResult.Success -> {
                if (relationshipState == 0) resultMessage = "Poisto onnistui"
                if (relationshipState == 1) resultMessage = "Kaveripyyntö lähetetty"
                if (relationshipState == 3) resultMessage = "Kaveripyyntö hyväksytty"
                shouldShowAlertDialog = false
                refreshFriendlist(false)
            }
            FriendlistResult.UserNotFound -> {
                resultMessage = "Käyttäjää ei löydy"
            }
        }
    }

    fun disableEditMode() {
        editModeEnabled = false
    }


    override fun onCleared() {
        Log.i("apu", "Clearattu viewmodel")
    }

}