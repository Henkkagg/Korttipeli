package com.example.korttipeli.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.korttipeli.domain.model.*

@Dao
interface DecksDao {

    @Insert
    suspend fun insertOne(deck: Deck)

    @Insert
    suspend fun insertMany(decks: List<Deck>)

    @Query("SELECT * FROM decks_table")
    suspend fun getAll(): List<Deck>

    @Query("SELECT * FROM decks_table WHERE id = :id")
    suspend fun getOneById(id: String): Deck?

    @Update(entity = Deck::class)
    suspend fun updateImages(updatedImageList: List<UpdatedImage>)

    @Update(entity = Deck::class)
    suspend fun updateNonImages(deckUpdatedNonImages: List<DeckUpdatedNonImage>)

    @Query("SELECT image FROM decks_table")
    suspend fun getImageLocations(): List<String>

    @Query("DELETE FROM decks_table WHERE id in (:idList)")
    suspend fun deleteByIds(idList: List<String>)
}