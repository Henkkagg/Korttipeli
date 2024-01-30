package com.example.korttipeli.data.repository

import com.example.korttipeli.data.HttpClientImpl
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.domain.model.Account
import com.example.korttipeli.domain.model.LoginDetails
import com.example.korttipeli.domain.repository.AccountManagementRepository

import com.example.korttipeli.domain.use_case.AccountManagementResult
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountManagementImpl @Inject constructor(
    httpClientImpl: HttpClientImpl,
    private val sharedPref: SharedPref
) : AccountManagementRepository {
    private val client = httpClientImpl.authenticated
    private val unAuthenticatedClient = httpClientImpl.unAuthenticated

    override suspend fun createNewAccount(account: Account): AccountManagementResult {
        return withContext(Dispatchers.IO) {

            val result = unAuthenticatedClient.post("/registration") {
                contentType(ContentType.Application.Json)
                setBody(account)
            }.status

            return@withContext when (result) {
                HttpStatusCode.OK -> AccountManagementResult.Success
                HttpStatusCode.BadRequest -> AccountManagementResult.IllegalCharacters
                HttpStatusCode.Conflict -> AccountManagementResult.UsernameTaken
                else -> AccountManagementResult.ServerError
            }
        }

    }


    override suspend fun logIn(loginDetails: LoginDetails): AccountManagementResult {
        val response = unAuthenticatedClient.post("/authentication/login") {
            contentType(ContentType.Application.Json)
            setBody(loginDetails)
        }
        runCatching {
            val tokens = response.body<BearerTokens>()
            sharedPref.saveTokens(tokens)
        }.onFailure { return AccountManagementResult.PasswordWrong }


        return when (response.status) {
            HttpStatusCode.OK -> AccountManagementResult.Success
            HttpStatusCode.NonAuthoritativeInformation -> AccountManagementResult.PasswordWrong
            else -> AccountManagementResult.ServerError
        }
    }
}
