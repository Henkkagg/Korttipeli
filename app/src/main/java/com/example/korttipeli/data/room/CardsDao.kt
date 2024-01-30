package com.example.korttipeli.data.room

import androidx.room.*
import com.example.korttipeli.domain.model.*

@Dao
interface CardsDao {

    @Query("SELECT * FROM cards_table")
    suspend fun getAll(): List<Card>

    @Query("SELECT * FROM cards_table WHERE id in (:idList)")
    suspend fun getCardsByIds(idList: List<String>): List<Card>

    @Query("SELECT id, id_image, id_text FROM cards_table")
    suspend fun getAllIds(): List<Ids>

    @Query("SELECT id FROM cards_table")
    suspend fun getCardIds(): List<String>

    @Query("SELECT id, id_image, image FROM cards_table WHERE id in (:idList)")
    suspend fun getImagesByIds(idList: List<String>): List<UpdatedImage>

    @Query("SELECT id, id_text, title, instructions, type FROM cards_table WHERE id in (:idList)")
    suspend fun getInfosByIds(idList: List<String>): List<UpdatedInfo>

    @Query("SELECT image FROM cards_table")
    suspend fun getImageLocations(): List<String>

    @Insert
    suspend fun insertOne(card: Card)

    @Insert
    suspend fun insertMany(cardList: List<Card>)

    @Update(entity = Card::class)
    suspend fun updateInfos(updatedInfoList: List<UpdatedInfo>)

    @Update(entity = Card::class)
    suspend fun updateImages(updatedImageList: List<UpdatedImage>)

    @Query("DELETE FROM cards_table WHERE id in (:idList)")
    suspend fun deleteByIds(idList: List<String>)

}