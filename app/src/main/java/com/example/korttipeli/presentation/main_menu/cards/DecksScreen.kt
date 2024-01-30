package com.example.korttipeli.presentation.main_menu.cards

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.korttipeli.R
import com.example.korttipeli.domain.model.Deck
import com.example.korttipeli.presentation.app.AppState
import com.example.korttipeli.presentation.destinations.DeckEditorScreenDestination
import com.example.korttipeli.presentation.destinations.DeckInspectorDestination
import com.example.korttipeli.presentation.main_menu.cards.deck_editor.TypeCounter
import com.example.korttipeli.ui.theme.*
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun DecksScreen(
    navigator: DestinationsNavigator,
    appState: AppState,
    viewModel: DecksViewModel = hiltViewModel(),
    creatingGame: Boolean = false,
    onPressed: (Deck) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        if (!creatingGame) {
            appState.showFab(Icons.Default.Add) { navigator.navigate(DeckEditorScreenDestination()) }
        }
        viewModel.onInit()
    }


    val decks by viewModel.decks.collectAsState()

    LazyColumn {

        itemsIndexed(decks) { index, deck ->
            val headerOrNull = viewModel.headerOrNull(index)
            if (headerOrNull != null) {
                Text(
                    text = headerOrNull,
                    style = MaterialTheme.typography.h1
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            DeckPreview(
                deck = deck,
                image = viewModel.loadImage(deck),
            ) {
                if (creatingGame) {
                    onPressed(it)
                } else navigator.navigate(DeckInspectorDestination(deck.id))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

}

@Composable
fun DeckPreview(
    deck: Deck,
    image: Bitmap,
    onPressed: (Deck) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .clip(MaterialTheme.cardShapes.preview)
            .clickable { onPressed(deck) },
        backgroundColor = DarkButtonBlue
    ) {
        Row(
            modifier = Modifier
                .padding(MaterialTheme.spacing.small),
        ) {

            val localDensity = LocalDensity.current
            var imageBoxHeight by remember { mutableStateOf(0.dp) }

            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .onGloballyPositioned {
                        imageBoxHeight = with(localDensity) { it.size.height.toDp() }
                    }
            ) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = deck.name,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.h3

                        .copy(
                            lineHeight = 1.5.em,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Top,
                                trim = LineHeightStyle.Trim.FirstLineTop
                            )
                        ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(BackgroundBlue.copy(0.8f))
                        .fillMaxWidth()
                        .drawBehind {

                            drawLine(
                                color = DarkButtonBlue,
                                start = Offset(0f, this.size.height),
                                end = Offset(this.size.width, this.size.height),
                                strokeWidth = 18f
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))

            Column(
                modifier = Modifier
                    .height(imageBoxHeight)
            ) {
                val typeModifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()

                TypeCounter(
                    modifier = typeModifier,
                    R.drawable.ic_action,
                    deck.types[0],
                    Grey,
                    true
                )
                TypeCounter(
                    modifier = typeModifier,
                    R.drawable.ic_virus,
                    deck.types[1],
                    Green,
                    true
                )
                TypeCounter(
                    modifier = typeModifier,
                    R.drawable.ic_secret,
                    deck.types[2],
                    Pink,
                    true
                )
            }
        }
    }
}