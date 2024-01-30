package com.example.korttipeli.data

import android.util.Log
import com.example.korttipeli.domain.use_case.UnhandledResponse
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.util.*

private val wsAttribute = AttributeKey<Boolean>("wss")
fun HttpRequestBuilder.websocket() {
    this.attributes.put(wsAttribute, true)
}

class HttpClientImpl(private val sharedPref: SharedPref) {

    var ipAndPort = ""
    var baseUrl = ""
    private var accessToken = sharedPref.readTokens().accessToken

    init {
        val firebaseReference = Firebase.database.reference
        firebaseReference.child("url").get().addOnSuccessListener {
            ipAndPort = it.value.toString()
            Log.i("apu", "Base url haettu: $ipAndPort")
        }.addOnFailureListener {
            Log.i("apu", "Base url:n haku ei onnistunut")
        }
    }

    val authenticated = HttpClient(CIO) {

        install(WebSockets)

        install(Auth) {
            bearer {
                loadTokens {
                    sharedPref.readTokens()
                }
                refreshTokens {
                    getAndSaveNewTokens()
                }
            }
        }
        install(ContentNegotiation) {
            gson()
        }
    }.apply {
        plugin(HttpSend).intercept { request ->

            val protocol = if (request.attributes.contains(wsAttribute)) "wss://" else "https://"
            val path = request.url.encodedPath
            request.url(protocol + ipAndPort + path)

            execute(request)
        }
    }

    val unAuthenticated = HttpClient(CIO) {
        defaultRequest {
            url("https://$ipAndPort")
        }
        install(ContentNegotiation) {
            gson()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 20000
            socketTimeoutMillis = 20000
        }
    }

    private suspend fun getAndSaveNewTokens(): BearerTokens {
        val newTokens = unAuthenticated.post("authentication/refresh") {
            header(HttpHeaders.Authorization, "Bearer ${sharedPref.readTokens().refreshToken}")
        }.body<BearerTokens>()

        sharedPref.saveTokens(newTokens)
        return newTokens
    }

}

suspend fun HttpResponse.toUnhandledResponse(): UnhandledResponse {
    return if (this.status == HttpStatusCode.OK) {
        UnhandledResponse.Success(this.body())
    } else {
        UnhandledResponse.Failure(this.status)
    }
}