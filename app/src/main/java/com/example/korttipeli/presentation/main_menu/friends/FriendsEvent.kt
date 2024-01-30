package com.example.korttipeli.presentation.main_menu.friends

sealed class FriendsEvent {
    object EditToggled : FriendsEvent()
    object RequestsToggled : FriendsEvent()
    object AlertdialogToggled : FriendsEvent()

    //3=friends, 2=request sent, 1=request received
    data class ConfirmRemovePressed(val username: String, val relationshipState: Int) : FriendsEvent()
    data class AddFriendPressed(val username: String, val relationshipState: Int) : FriendsEvent()
    data class UsernameTyped(val value: String) : FriendsEvent()
}