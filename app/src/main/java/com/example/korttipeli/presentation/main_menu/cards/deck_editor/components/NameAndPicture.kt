package com.example.korttipeli.presentation.main_menu.cards.deck_editor.components

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.korttipeli.presentation.common_components.CustomButton
import com.example.korttipeli.presentation.common_components.CustomOutlinedTextField
import com.example.korttipeli.presentation.main_menu.cards.deck_editor.DeckEditorViewModel
import com.example.korttipeli.presentation.main_menu.cards.deck_editor.UiEvent
import com.example.korttipeli.ui.theme.Grey
import com.example.korttipeli.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NameAndPicture(
    name: String,
    bitmap: Bitmap?,
    viewModel: DeckEditorViewModel
) {
    Column {
        CustomOutlinedTextField(
            value = name,
            label = "Nimi",
            inputAccepted = name.isNotBlank(),
            onValueChange = { viewModel.onEvent(UiEvent.NameTyped(it)) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Image(
            painter = rememberAsyncImagePainter(bitmap ?: R.drawable.placeholder_grey),
            contentDescription = "Pakan kuva",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(3.dp, Grey, RoundedCornerShape(20.dp))
                .aspectRatio(1.5f)
                .clickable { viewModel.onEvent(UiEvent.ImagePressed) },
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.Bottom
        ) {
            CustomButton(
                enabled = name.isNotBlank() && bitmap != null,
                text = "Siirry valitsemaan kortit"
            ) {
                viewModel.onEvent(UiEvent.ChooseCardsPressed)
            }
        }
    }
}