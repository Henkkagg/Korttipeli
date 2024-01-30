package com.example.korttipeli.presentation.main_menu.cards.card_inspector

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.presentation.app.AlertDialogState
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.common_components.CustomAlertDialog
import com.example.korttipeli.ui.theme.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun CardInspectorScreen(
    id: String,
    viewModel: CardInspectorViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<String>,
    appState: AppState,
    isInGame: Boolean = false,
    isUsingSecret: Boolean = false,
) {
    //Card id is "" when creating the card. Otherwise we are inspecting a card already in db
    BackHandler {
        when (viewModel.inspectorMode) {
            InspectorMode.InspectingOwn -> navigator.navigateUp()
            InspectorMode.InspectingFriends -> navigator.navigateUp()
            InspectorMode.EditingOwn -> viewModel.onEvent(CardInspectorEvent.CancelPressed)
            InspectorMode.CreatingNew -> navigator.navigateUp()
            InspectorMode.DuringGame -> navigator.navigateUp()
            InspectorMode.PreviewingChanges -> viewModel.onEvent(CardInspectorEvent.CancelPressed)
            InspectorMode.PreviewingNew -> viewModel.onEvent(CardInspectorEvent.CancelPressed)
            is InspectorMode.UsingSecret -> navigator.navigateUp()
        }
    }

    val alertDialogState = remember { AlertDialogState }
    if (viewModel.shouldShowAlertDialog) {
        CustomAlertDialog(
            message = "Kortin poistaminen poistaa sen myös kaikilta kavereiltasi",
            onConfirmText = alertDialogState.confirmText,
            onDismissText = alertDialogState.dismissText,
            onDismiss = { viewModel.onEvent(CardInspectorEvent.AlertDialogDismissed) },
            onConfirm = { viewModel.onEvent(CardInspectorEvent.AlertDialogConfirmed) })
    }

    if (viewModel.resultMessage != "") {
        appState.showSnackbar(viewModel.resultMessage)
        viewModel.resultMessageAcknowledged()
    }
    if (viewModel.shouldNavigateBack) {
        viewModel.navRequestAcknowledged()

        if (viewModel.inspectorMode == InspectorMode.UsingSecret) {
            resultNavigator.navigateBack(result = id)
        } else {
            navigator.navigateUp()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onInit(id, isInGame, isUsingSecret)
    }

    //Workaround for in-game back navigation by pressing image, so user can't press multiple times
    var stopFromNavigating = remember { false }

    val generalShape = remember { RoundedCornerShape(20.dp) }
    val borderWidth = remember { 3.dp }
    var backgroundColor by remember { mutableStateOf(Grey) }
    var borderColor by remember { mutableStateOf(BorderGrey) }

    when (viewModel.cardState.cardType) {
        //Action
        1 -> {
            backgroundColor = Grey
            borderColor = BorderGrey
        }
        //Virus
        2 -> {
            backgroundColor = Green
            borderColor = BorderGreen
        }
        //Secret
        3 -> {
            backgroundColor = Pink
            borderColor = BorderPink
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = { if (viewModel.shouldShowFab) CardEditorFab(viewModel, id) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .clip(generalShape)
                .background(backgroundColor)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = generalShape
                )
                .padding(10.dp)
        ) {
            val lazyListState = rememberLazyListState()
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                state = lazyListState
            ) {
                items(listOf("type", "image", "title", "description", "fabSpacer")) { item ->
                    when (item) {
                        "type" -> {
                            if (viewModel.inspectorMode.editorEnabled) {
                                CardTypeSectionEditor(viewModel)
                            } else CardTypeSection(viewModel)
                        }
                        "image" -> {
                            if (viewModel.inspectorMode.editorEnabled) {
                                CardImageSectionEditor(
                                    viewModel,
                                    generalShape,
                                    borderColor,
                                    borderWidth
                                )
                            } else CardImageSectionEditor(
                                viewModel,
                                generalShape,
                                borderColor,
                                borderWidth
                            ) {
                                if (isInGame && !stopFromNavigating) {
                                    stopFromNavigating = true
                                    navigator.navigateUp()
                                }
                            }
                        }
                        "title" -> {
                            if (viewModel.inspectorMode.editorEnabled) {
                                CardTitleSectionEditor(viewModel)
                            } else CardTitleSection(viewModel)
                        }
                        "description" -> {
                            if (viewModel.inspectorMode.editorEnabled) {
                                CardDescriptionSectionEditor(
                                    viewModel,
                                    lazyListState,
                                    appState.compositionScope
                                )
                            } else CardDescriptionSection(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardEditorFab(viewModel: CardInspectorViewModel, id: String) {
    val fabs = CardInspectorScreenFabs(viewModel)
    Column {
        when (viewModel.inspectorMode) {
            InspectorMode.CreatingNew -> fabs.ArrowForward()
            InspectorMode.PreviewingNew -> {
                fabs.Cancel()
                fabs.Spacer()
                fabs.Confirm()
            }

            InspectorMode.InspectingOwn -> {
                fabs.Delete()
                fabs.Spacer()
                fabs.Edit()
            }
            InspectorMode.EditingOwn -> {
                fabs.Cancel()
                fabs.Spacer()
                fabs.ArrowForward()
            }
            InspectorMode.PreviewingChanges -> {
                fabs.Cancel()
                fabs.Spacer()
                fabs.Confirm()
            }
            InspectorMode.UsingSecret -> {
                fabs.Confirm()
            }
            else -> {}
        }
    }
}

class CardInspectorScreenFabs(private val viewModel: CardInspectorViewModel) {

    @Composable
    fun ArrowForward() {
        FloatingActionButton(onClick = { viewModel.onEvent(CardInspectorEvent.ConfirmPressed) }) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Katso")
        }
    }

    @Composable
    fun Cancel() {
        FloatingActionButton(onClick = { viewModel.onEvent(CardInspectorEvent.CancelPressed) }) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = "Peruuta")
        }
    }

    @Composable
    fun Edit() {
        FloatingActionButton(onClick = { viewModel.onEvent(CardInspectorEvent.EditPressed) }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Muokkaa")
        }
    }

    @Composable
    fun Delete() {
        FloatingActionButton(onClick = { viewModel.onEvent(CardInspectorEvent.DeletePressed) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Poista")
        }
    }

    @Composable
    fun Confirm() {
        FloatingActionButton(onClick = { viewModel.onEvent(CardInspectorEvent.ConfirmPressed) }) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Vahvista")
        }
    }

    @Composable
    fun Spacer() {
        Spacer(modifier = Modifier.height(10.dp))
    }
}