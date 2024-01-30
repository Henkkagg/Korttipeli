package com.example.korttipeli.presentation.main_menu.cards.card_inspector

import android.graphics.Bitmap

data class CardState(
    val cardType: Int = 1,
    val titleContent: String = "",
    var descriptionContent: String = "",
    var bitmap: Bitmap? = null,
    val author: String = ""
)