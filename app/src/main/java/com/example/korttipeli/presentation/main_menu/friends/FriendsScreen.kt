package com.example.korttipeli.presentation.main_menu.friends

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.R
import com.example.korttipeli.presentation.app.AlertDialogState
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.common_components.CustomAlertDialog
import com.example.korttipeli.presentation.common_components.CustomOutlinedTextField
import com.example.korttipeli.presentation.destinations.FriendsInfoScreenDestination
import com.example.korttipeli.ui.theme.ButtonBlue
import com.example.korttipeli.ui.theme.Grey
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Destination
@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    appState: AppState
) {
    val spacerDp = remember(viewModel.editModeEnabled) {
        if (viewModel.editModeEnabled) 20.dp else 0.dp
    }

    val alertDialogState = remember { AlertDialogState }
    if (viewModel.shouldShowAlertDialog) CustomAlertDialog(
        message = alertDialogState.message,
        onConfirmText = alertDialogState.confirmText,
        onDismissText = alertDialogState.dismissText,
        onDismiss = { viewModel.onEvent(FriendsEvent.AlertdialogToggled) },
        onConfirm = {
            viewModel.onEvent(
                FriendsEvent.ConfirmRemovePressed(
                    username = AlertDialogState.actionTarget,
                    relationshipState = 0
                )
            )
            appState.compositionScope.launch {
                viewModel.resultJob.join()
                appState.showSnackbar(viewModel.resultMessage)
            }
        })

    LaunchedEffect(key1 = "refresh") {
        viewModel.refreshFriendlist(false)
        viewModel.disableEditMode()
    }

    BackHandler {
        viewModel.disableEditMode()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(FriendsEvent.EditToggled) }) {
                if (!viewModel.editModeEnabled) {
                    Icon(Icons.Default.Edit, contentDescription = "Muokkaa")
                } else Icon(Icons.Default.Done, contentDescription = "Valmis")
            }
        }
    ) { innerPadding ->


        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = viewModel.isRefreshing),
            onRefresh = { viewModel.refreshFriendlist(true) }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                item {
                    GreetingSection(navigator)

                    if (viewModel.editModeEnabled) {
                        AddNew(viewModel, appState.compositionScope, appState.scaffoldState)
                    }

                    FriendsWith(viewModel)

                    if (viewModel.relationshipList.sentRequests.isNotEmpty()
                        || viewModel.relationshipList.receivedRequests.isNotEmpty()
                    ) {
                        RequestTitle(viewModel)
                    }

                    if (viewModel.relationshipList.receivedRequests.isNotEmpty()
                        && viewModel.shouldShowRequests
                    ) {
                        ReceivedRequests(viewModel, spacerDp, appState)
                    }

                    if (viewModel.relationshipList.sentRequests.isNotEmpty()
                        && viewModel.shouldShowRequests
                    ) {
                        SentRequests(viewModel, spacerDp, appState)
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AddNew(
    viewModel: FriendsViewModel,
    compositionScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxWidth()) {
        CustomOutlinedTextField(
            value = viewModel.usernameContent,
            label = "Käyttäjätunnus",
            onValueChange = { viewModel.onEvent(FriendsEvent.UsernameTyped(it)) },
            inputAccepted = viewModel.usernameContent.isNotBlank()
        )
    }
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = ButtonBlue,
            disabledBackgroundColor = Grey
        ),
        enabled = viewModel.usernameContent.isNotBlank() && !viewModel.resultJob.isActive,
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            keyboardController?.hide()
            viewModel.onEvent(FriendsEvent.AddFriendPressed(viewModel.usernameContent, 1))

            compositionScope.launch {
                viewModel.resultJob.join()
                scaffoldState.snackbarHostState.showSnackbar(viewModel.resultMessage)
            }
        }
    ) {
        Text(text = "Lisää kaveriksi", color = Color.Black)
    }

}

@Composable
fun FriendsWith(
    viewModel: FriendsViewModel,
) {
    val listSize = viewModel.relationshipList.friendsWith.size
    val leftList = viewModel.relationshipList.friendsWith.dropLast(listSize / 2)
    val rightList = viewModel.relationshipList.friendsWith.takeLast(listSize / 2)

    Text(
        textAlign = TextAlign.Center,
        text = "Kaverit",
        style = MaterialTheme.typography.h1,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))

    Row {
        Column(modifier = Modifier.fillMaxWidth(0.5f)) {
            leftList.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (viewModel.editModeEnabled) {
                        IconButton(
                            onClick = {
                                AlertDialogState.actionTarget = it
                                AlertDialogState.message = "Poistetaanko $it kavereistasi? " +
                                        "Ette sen jälkeen näe toistenne kortteja ettekä pakkoja."
                                viewModel.onEvent(FriendsEvent.AlertdialogToggled)
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Poista")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(text = it)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            rightList.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = it)
                    if (viewModel.editModeEnabled) {
                        IconButton(
                            onClick = {
                                AlertDialogState.actionTarget = it
                                AlertDialogState.message = "Poistetaanko $it kavereistasi? " +
                                        "Ette sen jälkeen näe toistenne kortteja ettekä pakkoja."
                                viewModel.onEvent(FriendsEvent.AlertdialogToggled)
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Poista")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RequestTitle(viewModel: FriendsViewModel) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.onEvent(FriendsEvent.RequestsToggled) }
    ) {
        val (icon, text) = createRefs()
        val iconRotation = if (viewModel.shouldShowRequests) 180F else 0F

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .constrainAs(text) {
                    centerTo(parent)
                }
        ) {
            Text(
                text = "Pyynnöt",
                style = MaterialTheme.typography.h1
            )
        }
        Icon(
            Icons.Outlined.ArrowDropDown,
            contentDescription = "Näytä pyynnöt",
            modifier = Modifier
                .rotate(iconRotation)
                .constrainAs(icon) {
                    end.linkTo(text.absoluteLeft)
                }
        )
    }
}

@Composable
fun ReceivedRequests(
    viewModel: FriendsViewModel,
    spacerDp: Dp,
    appState: AppState
) {
    Text(
        textAlign = TextAlign.Center,
        text = "Vastaanotetut",
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
    viewModel.relationshipList.receivedRequests.forEach {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewModel.editModeEnabled) {
                IconButton(
                    onClick = {
                        viewModel.onEvent(FriendsEvent.AddFriendPressed(it, 3))
                        appState.compositionScope.launch {
                            viewModel.resultJob.join()
                            appState.showSnackbar(viewModel.resultMessage)
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Outlined.Done, contentDescription = "Hyväksy")
                }
                IconButton(
                    onClick = { viewModel.onEvent(FriendsEvent.ConfirmRemovePressed(it, 3)) }
                ) {
                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Poista")
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(text = it)
        }
        Spacer(modifier = Modifier.height(spacerDp))
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun SentRequests(
    viewModel: FriendsViewModel,
    spacerDp: Dp,
    appState: AppState
) {
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        textAlign = TextAlign.Center,
        text = "Lähetetyt",
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(10.dp))
    viewModel.relationshipList.sentRequests.forEach {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (viewModel.editModeEnabled) {
                IconButton(
                    onClick = {
                        viewModel.onEvent(FriendsEvent.ConfirmRemovePressed(it, 0))

                        appState.compositionScope.launch {
                            viewModel.resultJob.join()
                            appState.showSnackbar(viewModel.resultMessage)
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Poista")
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(text = it)
        }
        Spacer(modifier = Modifier.height(spacerDp))
    }
}

@Composable
fun GreetingSection(navigator: DestinationsNavigator) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.friendlist),
                contentDescription = "Kaverit",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    navigator.navigate(FriendsInfoScreenDestination)
                }) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Lisätiedot"
                )
            }
        }
    }
}
