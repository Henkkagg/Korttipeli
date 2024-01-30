package com.example.korttipeli.presentation.authentication.registration

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.domain.use_case.AccountManagementResult
import com.example.korttipeli.domain.use_case.AccountManagementUsecases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val accountManagementUsecases: AccountManagementUsecases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var usernameContent: String by mutableStateOf("")
        private set
    var emailContent: String by mutableStateOf("")
        private set
    var password1Content: String by mutableStateOf("")
        private set
    var password2Content: String by mutableStateOf("")
        private set
    var termsAccepted: Boolean by mutableStateOf(false)
        private set
    var usernameRequirementsMet: Boolean by mutableStateOf(false)
        private set
    var passwordRequirementsMet: Boolean by mutableStateOf(false)
        private set
    var emailRequirementsMet: Boolean by mutableStateOf(false)
        private set
    var allRequirementsMet: Boolean by mutableStateOf(false)
        private set
    var registrationInProgress: Boolean by mutableStateOf(false)
        private set
    var resultMessage: String by mutableStateOf("")
        private set
    var navigateToLogin: Boolean by mutableStateOf(false)
        private set

    lateinit var resultJob: Job
        private set

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.UsernameTyped -> {
                usernameContent = event.value
                usernameRequirementsMet = usernameContent.isNotBlank()
            }
            is RegistrationEvent.EmailTyped -> {
                emailContent = event.value
                emailRequirementsMet =
                    android.util.Patterns.EMAIL_ADDRESS.matcher(emailContent).matches()
            }
            is RegistrationEvent.Password1Typed -> {
                password1Content = event.value
                passwordRequirementsMet =
                    password1Content == password2Content && password1Content.isNotEmpty()
            }
            is RegistrationEvent.Password2Typed -> {
                password2Content = event.value
                passwordRequirementsMet =
                    password1Content == password2Content && password1Content.isNotEmpty()
            }
            is RegistrationEvent.TermsToggled -> {
                termsAccepted = event.value
            }
            is RegistrationEvent.RegisterPressed -> {
                if (!allRequirementsMet) return

                registrationInProgress = true
                resultJob = viewModelScope.launch {
                    val result = accountManagementUsecases.registration(
                        usernameContent,
                        password1Content,
                        emailContent
                    )
                    performActionByResult(result)
                    registrationInProgress = false
                }
            }
        }
        allRequirementsMet =
            usernameRequirementsMet && passwordRequirementsMet
                    && emailRequirementsMet && termsAccepted && !registrationInProgress
    }

    private fun performActionByResult(result: AccountManagementResult) {
        allRequirementsMet = true
        when (result) {
            is AccountManagementResult.Success -> {
                navigateToLogin = true
            }
            is AccountManagementResult.IllegalCharacters -> {
                resultMessage = "Käyttäjätunnus saa sisältää vain kirjaimia ja numeroita"
            }
            is AccountManagementResult.UsernameTaken -> {
                resultMessage = "Käyttäjätunnus on jo käytössä"
            }
            else -> {
                resultMessage = "Tuntematon virhe"
            }
        }
        Log.i("apu", resultMessage)
    }
}