package com.example.korttipeli.presentation.main_menu.friends

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun FriendsInfoScreen(navigator: DestinationsNavigator) {

    BackHandler {
        navigator.navigateUp()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Lisätiedot",
            style = MaterialTheme.typography.h1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Kun toinen käyttäjä hyväksyy lähettämäsi kaveripyynnön, jaatte toisillenne automaattisesti kaikki tekemänne kortit ja pakat.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Jos jompi kumpi poistaa toisensa kavereista, jaetut kortit ja pakat katoavat. Toisen henkilön kortit katoavat myös silloin, jos ne sisältyvät itse tehtyyn pakkaan",
            textAlign = TextAlign.Center
        )
    }
}