package com.example.korttipeli.domain.model

data class Account(
    val username: String,
    val password: String,
    val email: String = ""
)
