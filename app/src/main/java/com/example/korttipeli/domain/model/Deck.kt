@file:Suppress("ArrayInDataClass")

package com.example.korttipeli.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "decks_table")
data class Deck(
    @PrimaryKey val id: String = "",
    @ColumnInfo(name = "id_image") val idForImage: String = "",
    @ColumnInfo(name = "id_text") val idForNonImage: String = "",
    val name: String = "EEEEIIII",
    val image: String = "",
    val author: String = "",
    @ColumnInfo(name = "card_list") val cardList: List<String> = emptyList(),
) {
    @Ignore var types: IntArray = intArrayOf(0, 0, 0)

    fun toIds() = Ids(this.id, this.idForImage, this.idForNonImage)
}

data class DeckUpdatedNonImage(
    val id: String,
    @ColumnInfo(name = "id_text") val idForNonImage: String,
    val name: String,
    @ColumnInfo(name = "card_list") val cardList: List<String>,
)

data class DecksDataPackage(
    val newDecks: List<Deck>,
    val updatedImages: List<UpdatedImage>,
    val updatedNonImages: List<DeckUpdatedNonImage>,
    val decksToDelete: List<String>
)

//Used to send new or updated decks to server. In case we update, we use deckId. Leave blank if new
data class DeckToServer(
    val deckId: String = "",
    val name: String,
    val base64Image: String,
    val cardIds: List<String>
)