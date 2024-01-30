package com.example.korttipeli.presentation.main_menu.cards.card_inspector

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.korttipeli.R

@Composable
fun CardTypeSection(viewModel: CardInspectorViewModel) {
    var text: String by remember { mutableStateOf("Toiminto") }
    var icon: Int by remember { mutableStateOf(R.drawable.ic_action) }
    when (viewModel.cardState.cardType) {
        1 -> {
            text = "Toiminto"
            icon = R.drawable.ic_action
        }
        2 -> {
            text = "Virus"
            icon = R.drawable.ic_virus
        }
        3 -> {
            text = "Salaisuus"
            icon = R.drawable.ic_secret
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(text = text, style = MaterialTheme.typography.body1)
        Spacer(Modifier.width(10.dp))
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.size(20.dp)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun CardTypeSectionEditor(viewModel: CardInspectorViewModel) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        val modifier = Modifier
            .fillMaxWidth()
            .weight(1f)

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Toiminto")
            RadioButton(
                selected = viewModel.cardState.cardType == 1,
                onClick = { viewModel.onEvent(CardInspectorEvent.TypeChanged(1)) })
        }
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Virus")
            RadioButton(
                selected = viewModel.cardState.cardType == 2,
                onClick = { viewModel.onEvent(CardInspectorEvent.TypeChanged(2)) })
        }
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Salainen")
            RadioButton(
                selected = viewModel.cardState.cardType == 3,
                onClick = { viewModel.onEvent(CardInspectorEvent.TypeChanged(3)) })
        }
    }
}