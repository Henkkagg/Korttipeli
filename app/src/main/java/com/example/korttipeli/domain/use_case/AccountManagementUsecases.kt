package com.example.korttipeli.domain.use_case

import com.example.korttipeli.domain.use_case.account_management.Login
import com.example.korttipeli.domain.use_case.account_management.Registration
import javax.inject.Inject

data class AccountManagementUsecases @Inject constructor(
    val login: Login,
    val registration: Registration
)

sealed class AccountManagementResult {
    object Success : AccountManagementResult()
    object ServerError : AccountManagementResult()
    object UsernameTaken : AccountManagementResult()
    object IllegalCharacters : AccountManagementResult()
    object PasswordWrong : AccountManagementResult()
}