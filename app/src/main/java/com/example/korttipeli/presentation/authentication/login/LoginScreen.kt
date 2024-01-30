package com.example.korttipeli.presentation.authentication.login

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.R
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.common_components.CustomOutlinedTextField
import com.example.korttipeli.presentation.common_components.KeyboardSettings
import com.example.korttipeli.presentation.destinations.PlayScreenDestination
import com.example.korttipeli.presentation.destinations.RegistrationScreenDestination
import com.example.korttipeli.ui.theme.ButtonBlue
import com.example.korttipeli.ui.theme.Grey
import com.example.korttipeli.ui.theme.spacing
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<RegistrationScreenDestination, Boolean>,
    viewModel: LoginViewModel = hiltViewModel(),
    appState: AppState
) {
    resultRecipient.onNavResult {
        if (it == NavResult.Value(true)) appState.showSnackbar("Rekisteröinti onnistui")
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GreetingSection()
        Spacer(modifier = Modifier.height(10.dp))
        InputSection(navigator, viewModel, appState.compositionScope, appState.scaffoldState)
    }
}



@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun InputSection(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel,
    compositionScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    CustomOutlinedTextField(
        value = viewModel.usernameContent,
        label = "Käyttäjätunnus",
        imeAction = ImeAction.Next,
        inputAccepted = viewModel.usernameRequirementsMet,
        onValueChange = { viewModel.onEvent(LoginEvent.UsernameTyped(it)) }
    )
    CustomOutlinedTextField(
        value = viewModel.passwordContent,
        label = "Salasana",
        imeAction = ImeAction.Go,
        inputAccepted = viewModel.passwordRequirementsMet,
        keyboardSettings = KeyboardSettings.Password,
        onValueChange = { viewModel.onEvent(LoginEvent.PasswordTyped(it)) },
        onImeActionPressed = { Log.i("apu", "Painoit valmisss!!") }
    )
    Spacer(modifier = Modifier.height(10.dp))
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = ButtonBlue,
            disabledBackgroundColor = Grey
        ),
        enabled = viewModel.allRequirementsMet,
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            viewModel.onEvent(LoginEvent.LoginPressed)
            keyboardController?.hide()

            compositionScope.launch {
                viewModel.resultJob.join()
                if (viewModel.navigateToMainMenu) {
                    navigator.popBackStack()
                    navigator.navigate(PlayScreenDestination)

                } else {
                    scaffoldState.snackbarHostState.showSnackbar(viewModel.resultMessage)
                }
            }


        }
    ) {
        Text(
            text = "Kirjaudu sisään",
            color = Color.Black
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.extraLarge)
    ) {
        Text(
            text = "Unohdin salasanan (kehityksen alla)",
            style = TextStyle(textDecoration = TextDecoration.Underline),
            color = Color.Blue,
            modifier = Modifier
                .clickable { viewModel.onEvent(LoginEvent.ForgotPasswordPressed) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Rekisteröidy tästä",
            style = TextStyle(textDecoration = TextDecoration.Underline),
            color = Color.Blue,
            modifier = Modifier
                .clickable {
                    navigator.navigate(RegistrationScreenDestination)
                    viewModel.onEvent(LoginEvent.RegisterPressed)
                }
        )
    }
}

@Composable
private fun GreetingSection() {
    Image(
        painter = painterResource(id = R.drawable.drinkset),
        contentDescription = "Teekannuja",
        modifier = Modifier
            .fillMaxWidth(0.5f)
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "Tervetuloa Korttipeliin",
        style = MaterialTheme.typography.h1
    )
}
