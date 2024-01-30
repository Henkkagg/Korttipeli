package com.example.korttipeli.presentation.authentication.registration

sealed class RegistrationEvent {
    data class UsernameTyped(val value: String) : RegistrationEvent()
    data class EmailTyped(val value: String) : RegistrationEvent()
    data class Password1Typed(val value: String) : RegistrationEvent()
    data class Password2Typed(val value: String) : RegistrationEvent()
    data class TermsToggled(val value: Boolean) : RegistrationEvent()
    object RegisterPressed: RegistrationEvent()
}
