package com.example.korttipeli.presentation.common_components

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.korttipeli.R
import com.example.korttipeli.presentation.app.rememberAppState
import com.example.korttipeli.presentation.appCurrentDestinationAsState
import com.example.korttipeli.presentation.destinations.CardsDecksRootDestination
import com.example.korttipeli.presentation.destinations.FriendsScreenDestination
import com.example.korttipeli.presentation.destinations.PlayScreenDestination
import com.example.korttipeli.ui.theme.ButtonBlue
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@Composable
fun BottomBar(navController: NavController) {

    val currentDestination = navController.appCurrentDestinationAsState()

    val appState = rememberAppState()

    BackHandler {
    }

    BottomNavigation(
        backgroundColor = ButtonBlue
    ) {
        BottomBarDestination.values().forEach {
            BottomNavigationItem(
                selected = it.destination.route == currentDestination.value?.route,
                icon = {
                    Icon(painterResource(it.icon), it.label)
                },
                label = { Text(it.label) },
                onClick = {
                    Log.i("apu", "${navController.backQueue}")
                    navController.navigate(it.destination) {
                        popUpTo(
                            navController.currentDestination?.route
                                ?: PlayScreenDestination.route
                        ) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                unselectedContentColor = Color(0f, 0f, 0f, 0.3f),
            )
        }
    }

}

enum class BottomBarDestination(
    val destination: DirectionDestinationSpec,
    val icon: Int,
    val label: String
) {
    Play(PlayScreenDestination, R.drawable.ic_play, "Pelaa"),
    Cards(CardsDecksRootDestination, R.drawable.ic_cards, "Kortit"),
    Friends(FriendsScreenDestination, R.drawable.ic_friends, "Kaverit"),
    //Settings(SettingsScreenDestination, R.drawable.ic_settings, "Asetukset")
}
