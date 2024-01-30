package com.example.korttipeli.presentation.authentication.registration

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.R
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.common_components.CustomOutlinedTextField
import com.example.korttipeli.presentation.common_components.KeyboardSettings
import com.example.korttipeli.ui.theme.ButtonBlue
import com.example.korttipeli.ui.theme.Grey
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination
@Composable
fun RegistrationScreen(
    resultNavigator: ResultBackNavigator<Boolean>,
    viewModel: RegistrationViewModel = hiltViewModel(),
    appState: AppState
) {
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            GreetingSection()
            Spacer(modifier = Modifier.height(10.dp))
            InputSection(resultNavigator, viewModel, appState.compositionScope, appState.scaffoldState)
        }
    }
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
private fun InputSection(
    resultNavigator: ResultBackNavigator<Boolean>,
    viewModel: RegistrationViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    CustomOutlinedTextField(
        value = viewModel.usernameContent,
        label = "Käyttäjätunnus",
        bringIntoViewRequester = bringIntoViewRequester,
        inputAccepted = viewModel.usernameRequirementsMet,
        imeAction = ImeAction.Next,
        onValueChange = { viewModel.onEvent(RegistrationEvent.UsernameTyped(it)) }
    )
    CustomOutlinedTextField(
        value = viewModel.emailContent,
        label = "Sähköpostiosoite",
        keyboardSettings = KeyboardSettings.Email,
        bringIntoViewRequester = bringIntoViewRequester,
        inputAccepted = viewModel.emailRequirementsMet,
        imeAction = ImeAction.Next,
        onValueChange = { viewModel.onEvent(RegistrationEvent.EmailTyped(it)) }
    )
    CustomOutlinedTextField(
        value = viewModel.password1Content,
        label = "Salasana",
        keyboardSettings = KeyboardSettings.Password,
        bringIntoViewRequester = bringIntoViewRequester,
        inputAccepted = viewModel.passwordRequirementsMet,
        imeAction = ImeAction.Next,
        onValueChange = { viewModel.onEvent(RegistrationEvent.Password1Typed(it)) }
    )
    CustomOutlinedTextField(
        value = viewModel.password2Content,
        label = "Syötä salasana uudelleen",
        keyboardSettings = KeyboardSettings.Password,
        onValueChange = { viewModel.onEvent(RegistrationEvent.Password2Typed(it)) },
        bringIntoViewRequester = bringIntoViewRequester,
        inputAccepted = viewModel.passwordRequirementsMet,
        imeAction = ImeAction.Go,
        onImeActionPressed = { viewModel.onEvent(RegistrationEvent.RegisterPressed) }
    )
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)
    ) {
        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
            Checkbox(
                checked = viewModel.termsAccepted,
                colors = CheckboxDefaults.colors(checkmarkColor = Color.Black),
                onCheckedChange = { viewModel.onEvent(RegistrationEvent.TermsToggled(it)) }
            )
        }
        Text(
            text = "Ymmärrän, että salasanan palautus ei toimi vielä",
            style = MaterialTheme.typography.subtitle2
        )
    }
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = ButtonBlue,
            disabledBackgroundColor = Grey
        ),
        enabled = viewModel.allRequirementsMet,
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester),
        onClick = {
            viewModel.onEvent(RegistrationEvent.RegisterPressed)
            keyboardController?.hide()

            scope.launch {
                viewModel.resultJob.join()
                if (viewModel.navigateToLogin) {
                    resultNavigator.navigateBack(result = true)
                } else {
                    scaffoldState.snackbarHostState.showSnackbar(viewModel.resultMessage)
                }
            }
        }
    ) {
        Text(
            text = "Rekisteröidy",
            color = Color.Black
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