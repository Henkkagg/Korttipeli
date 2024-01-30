package com.example.korttipeli.presentation.authentication.login

sealed class LoginEvent {
    data class UsernameTyped(val value: String) : LoginEvent()
    data class PasswordTyped(val value: String) : LoginEvent()
    object LoginPressed : LoginEvent()
    object ForgotPasswordPressed : LoginEvent()
    object RegisterPressed : LoginEvent()
}
