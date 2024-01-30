package com.example.korttipeli.domain.repository

import com.example.korttipeli.domain.model.Account
import com.example.korttipeli.domain.model.LoginDetails
import com.example.korttipeli.domain.use_case.AccountManagementResult

interface AccountManagementRepository {

    suspend fun createNewAccount(account: Account): AccountManagementResult

    suspend fun logIn(loginDetails: LoginDetails): AccountManagementResult
}

