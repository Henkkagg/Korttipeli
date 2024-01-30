package com.example.korttipeli.domain.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards_table")
data class Card(
    @PrimaryKey val id: String,
    //Ids verify that the corresponding data is still up-to-date in the cached card.
    //If data has been changed, Ids help client to only update the relevant part of the card.
    @ColumnInfo(name = "id_image") val idForImage: String,
    @ColumnInfo(name = "id_text") val idForNonImage: String,

    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "instructions") val description: String,
    @ColumnInfo(name = "image") val image: String,
    //1=action, 2=virus, 3=secret
    @ColumnInfo(name = "type") val type: Int
)

data class CardUi(
    val id: String,
    val author: String = "",
    val title: String,
    val description: String = "",
    val image: Bitmap,
    val type: Int
)

//Information that client sends to server when creating/updating card. Image is sent separately
data class CardInfo(
    //id is empty when creating a new card
    val id: String = "",
    val title: String,
    val description: String,
    //1=action, 2=virus, 3=secret
    val type: Int,
    //imageId is empty when only updating text on existing card
    val imageId: String = ""
)

data class Ids(
    val id: String,
    @ColumnInfo(name = "id_image") val idForImage: String,
    @ColumnInfo(name = "id_text") val idForNonImage: String
)

data class UpdatedImage(
    val id: String,
    @ColumnInfo(name = "id_image") val idForImage: String,
    @ColumnInfo(name = "image") val image: String
)

data class UpdatedInfo(
    val id: String,
    @ColumnInfo(name = "id_text") val idForOtherThanImage: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "instructions") val description: String,
    //1=action, 2=virus, 3=secret
    @ColumnInfo(name = "type") val type: Int
)

data class CardDataPackage(
    val cardsToDelete: List<String>,
    val newCards: List<Card>,
    val updatedInfos: List<UpdatedInfo>,
    val updatedImages: List<UpdatedImage>
)

data class CardDataPackageIds(
    val cardsToDelete: List<String>,
    val newCards: List<String>,
    val updatedInfos: List<String>,
    val updatedImages: List<String>
)
