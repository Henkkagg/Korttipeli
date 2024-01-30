package com.example.korttipeli.presentation.app

import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.korttipeli.presentation.common_components.BottomBarDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppState(
    val scaffoldState: ScaffoldState,
    val compositionScope: CoroutineScope,
    val navController: NavController
) {
    var shouldShowBottomBar: Boolean by mutableStateOf(false)
        private set
    var fabState: FabState by mutableStateOf(FabState())
        private set

    init {
        navController.addOnDestinationChangedListener { _, navControllerDestination, _ ->
            fabState = fabState.copy(isShown = false)
            shouldShowBottomBar = BottomBarDestination.values().asList().any { bottomBar ->
                bottomBar.destination.route == navControllerDestination.route
            }
        }
    }

    fun showFab(imageVector: ImageVector, action: () -> Unit) {
        fabState = FabState(isShown = true, icon = imageVector, action = action)
    }

    fun showSnackbar(message: String) {
        compositionScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    compositionScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavController = rememberNavController()
): AppState {
    return remember {
        AppState(scaffoldState, compositionScope, navController)
    }
}

data class FabState(
    val isShown: Boolean = false,
    val icon: ImageVector = Icons.Default.Add,
    val action: () -> Unit = {}
)

@Composable
fun Fab(
    appState: AppState,
    imageVector: ImageVector,
    action: () -> Unit
) {
    LaunchedEffect(Unit) {
        appState.showFab(imageVector, action)
    }
}


