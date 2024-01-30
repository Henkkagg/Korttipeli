package com.example.korttipeli.presentation.main_menu.cards.card_inspector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.korttipeli.presentation.common_components.CustomOutlinedTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CardDescriptionSection(viewModel: CardInspectorViewModel) {
    Text(
        text = viewModel.cardState.descriptionContent,
        style = MaterialTheme.typography.body1,
        textAlign = TextAlign.Justify
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "- " + viewModel.cardState.author + " -",
        style = MaterialTheme.typography.subtitle2,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardDescriptionSectionEditor(
    viewModel: CardInspectorViewModel,
    lazyListState: LazyListState,
    scope: CoroutineScope
) {
    CustomOutlinedTextField(
        value = viewModel.cardState.descriptionContent,
        label = "Kuvaus",
        inputAccepted = viewModel.cardState.descriptionContent.isNotBlank(),
        singleLine = false,
        onValueChange = {
            viewModel.onEvent(CardInspectorEvent.DescriptionTyped(it))
            scope.launch { lazyListState.scrollToItem(4) }
        }
    )

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "- " + viewModel.cardState.author + " -",
        style = MaterialTheme.typography.subtitle2,
    )
}
