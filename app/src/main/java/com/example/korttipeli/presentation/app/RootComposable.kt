package com.example.korttipeli.presentation.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.korttipeli.presentation.NavGraphs
import com.example.korttipeli.presentation.common_components.BottomBar
import com.example.korttipeli.ui.theme.BackgroundBlue
import com.example.korttipeli.ui.theme.JuomapeliTheme
import com.example.korttipeli.ui.theme.spacing
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency

@Composable
fun RootComposable() {
    JuomapeliTheme {

        val appState = rememberAppState()
        val fabState = remember(appState.fabState) { appState.fabState }

        Scaffold(
            scaffoldState = appState.scaffoldState,
            bottomBar = {
                if (appState.shouldShowBottomBar) BottomBar(navController = appState.navController)
            },
            floatingActionButton = {
                if (fabState.isShown) {
                    FloatingActionButton(onClick = fabState.action) {
                        Icon(imageVector = fabState.icon, contentDescription = "FAB")
                    }
                }
            }
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .background(BackgroundBlue)
                    .padding(MaterialTheme.spacing.medium)
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    navController = appState.navController as NavHostController,
                    dependenciesContainerBuilder = {
                        dependency(appState)
                    }
                )
            }
        }
    }
}