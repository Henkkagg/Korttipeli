package com.example.korttipeli.domain.use_case.account_management

import com.example.korttipeli.domain.model.Account
import com.example.korttipeli.domain.repository.AccountManagementRepository
import com.example.korttipeli.domain.use_case.AccountManagementResult
import com.example.korttipeli.domain.util.PasswordHashGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Registration @Inject constructor(
    private val repository: AccountManagementRepository
) {
    suspend operator fun invoke(
        username: String, password: String, email: String
    ): AccountManagementResult {

        val account = Account(
            username = username,
            password = PasswordHashGenerator(password),
            email = email
        )
        return withContext(Dispatchers.IO) {
            repository.createNewAccount(account)
        }
    }


}