package com.example.korttipeli.domain.use_case.account_management

import com.example.korttipeli.domain.model.LoginDetails
import com.example.korttipeli.domain.repository.AccountManagementRepository
import com.example.korttipeli.domain.use_case.AccountManagementResult
import com.example.korttipeli.domain.util.PasswordHashGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Login @Inject constructor(
    private val repository: AccountManagementRepository
) {
    suspend operator fun invoke(username: String, password: String): AccountManagementResult {
        val hashedPassword = PasswordHashGenerator(password)
        val loginDetails = LoginDetails(username, hashedPassword)

        return withContext(Dispatchers.IO) {
            repository.logIn(loginDetails)
        }
    }
}