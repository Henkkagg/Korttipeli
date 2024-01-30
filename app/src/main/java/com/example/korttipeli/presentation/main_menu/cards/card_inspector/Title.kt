package com.example.korttipeli.presentation.main_menu.cards.card_inspector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.korttipeli.presentation.common_components.CustomOutlinedTextField

@Composable
fun CardTitleSection(viewModel: CardInspectorViewModel) {
    Text(text = viewModel.cardState.titleContent, style = MaterialTheme.typography.h2)
    Spacer(modifier = Modifier.height(10.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardTitleSectionEditor(
    viewModel: CardInspectorViewModel
) {
    CustomOutlinedTextField(
        value = viewModel.cardState.titleContent,
        label = "Nimi",
        onValueChange = { viewModel.onEvent(CardInspectorEvent.TitleTyped(it)) },
        inputAccepted = viewModel.cardState.titleContent.isNotBlank()
    )
}