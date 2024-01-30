package com.example.korttipeli.presentation.authentication.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korttipeli.data.SharedPref
import com.example.korttipeli.domain.use_case.AccountManagementResult
import com.example.korttipeli.domain.use_case.AccountManagementUsecases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountManagementUsecases: AccountManagementUsecases,
    private val sharedPref: SharedPref,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var usernameContent: String by mutableStateOf("")
        private set
    var passwordContent: String by mutableStateOf("")
        private set
    var usernameRequirementsMet: Boolean by mutableStateOf(false)
        private set
    var passwordRequirementsMet: Boolean by mutableStateOf(false)
        private set
    var allRequirementsMet: Boolean by mutableStateOf(false)
        private set
    var resultMessage: String by mutableStateOf("")
        private set
    var navigateToMainMenu: Boolean by mutableStateOf(false)
        private set

    lateinit var resultJob: Job
        private set

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameTyped -> {
                usernameContent = event.value
                usernameRequirementsMet = usernameContent.isNotBlank()
                checkIfActionRequirementsMet()
            }
            is LoginEvent.PasswordTyped -> {
                passwordContent = event.value
                passwordRequirementsMet = passwordContent.isNotEmpty()
                checkIfActionRequirementsMet()
            }
            is LoginEvent.LoginPressed -> {
                allRequirementsMet = false
                resultJob = viewModelScope.launch {
                    val result = accountManagementUsecases.login(usernameContent, passwordContent)
                    performActionByResult(result)
                }

            }
            is LoginEvent.RegisterPressed -> {
                usernameContent = ""
                passwordContent = ""
                usernameRequirementsMet = false
                passwordRequirementsMet = false
                allRequirementsMet = false
            }
            LoginEvent.ForgotPasswordPressed -> {
                allRequirementsMet = false
                viewModelScope.launch {
                }
            }
        }
    }

    private fun performActionByResult(result: AccountManagementResult) {
        allRequirementsMet = true
        when (result) {
            is AccountManagementResult.Success -> {
                sharedPref.saveUsername(usernameContent.trim())
                navigateToMainMenu = true
            }

            is AccountManagementResult.PasswordWrong -> {
                resultMessage = "Väärä salasana"
            }
            else -> {
                resultMessage = "Tuntematon virhe"
            }
        }
    }

    private fun checkIfActionRequirementsMet() {
        allRequirementsMet = usernameContent.isNotBlank() && passwordContent.isNotEmpty()
    }
}