package com.example.korttipeli.presentation.main_menu.play

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.R
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.app.Fab
import com.example.korttipeli.presentation.destinations.CreateLobbyScreenDestination
import com.example.korttipeli.presentation.destinations.GameScreenDestination
import com.example.korttipeli.ui.theme.ButtonBlue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun PlayScreen(
    appState: AppState,
    navigator: DestinationsNavigator,
    viewModel: PlayScreenViewModel = hiltViewModel()
) {
    Fab(
        appState = appState,
        imageVector = Icons.Default.Add
    ) {
        viewModel.onEvent(UiEvent.CreatePressed)
    }

    LaunchedEffect(Unit) {
        viewModel.onInit()
    }

    LaunchedEffect(viewModel.navigateTo) {
        when (viewModel.navigateTo) {
            NavigateTo.CreateNew -> navigator.navigate(CreateLobbyScreenDestination)
            is NavigateTo.Existing -> {
                navigator.navigate(
                    GameScreenDestination((viewModel.navigateTo as NavigateTo.Existing).gameId)
                )
            }
            NavigateTo.Nowhere -> Unit
        }
        viewModel.rogerNavigation()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.game),
            contentDescription = "Peli",
            modifier = Modifier.fillMaxHeight(0.25f)
        )
        Text(
            text = "Pelit",
            style = MaterialTheme.typography.h1
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (viewModel.games.isEmpty()) {
            Text(text = "Yhtään peliä ei löytynyt. Luo sinä peli ja kutsu kaverisi pelaamaan!")
        }

        LazyColumn {
            itemsIndexed(viewModel.games) { _, game ->
                GameCard(
                    name = game.name,
                    owner = game.owner,
                    playerCount = game.players.size
                ) {
                    viewModel.onEvent(UiEvent.JoinPressed(game))
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }

}

@Composable
private fun GameCard(
    name: String,
    owner: String,
    playerCount: Int,
    onPressed: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onPressed() },
        backgroundColor = ButtonBlue
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = name,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, "Omistaja")
                    Text(
                        text = owner,
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, "Pelaajamäärä")
                    Text(
                        text = playerCount.toString(),
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}