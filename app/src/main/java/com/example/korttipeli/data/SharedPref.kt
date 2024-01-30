package com.example.korttipeli.data

import android.content.Context
import com.example.korttipeli.domain.use_case.card.SortingSetting
import com.google.gson.Gson
import io.ktor.client.plugins.auth.providers.*

class SharedPref(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("tokenPref", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun saveTokens(tokens: BearerTokens) {
        editor.apply {
            putString("accessToken", tokens.accessToken)
            putString("refreshToken", tokens.refreshToken)
            apply()
        }
    }

    fun readTokens(): BearerTokens {
        val accessToken = sharedPreferences.getString("accessToken", "nothing saved") ?: "null"
        val refreshToken = sharedPreferences.getString("refreshToken", "nothing saved") ?: "null"

        return BearerTokens(accessToken, refreshToken)
    }

    fun saveUsername(username: String) {
        editor.apply {
            putString("username", username.capitalize().trim())
            apply()
        }
    }

    fun readUsername(): String  {
        return sharedPreferences.getString("username", "") ?: ""
    }

    fun saveSortingSettings(sortingSetting: SortingSetting) {
        val json = Gson().toJson(sortingSetting)
        editor.apply {
            putString("sortingsetting", json)
            apply()
        }
    }

    fun readSortingSettings(): SortingSetting {
        val sortingSetting = sharedPreferences.getString("sortingsetting", "")

        return if (sortingSetting != "") {
            Gson().fromJson(sortingSetting, SortingSetting::class.java)
        } else SortingSetting()
    }

}
