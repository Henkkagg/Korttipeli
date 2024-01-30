package com.example.korttipeli.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.korttipeli.domain.model.Card
import com.example.korttipeli.domain.model.Deck

@Database(entities = [Card::class, Deck::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database: RoomDatabase() {
    abstract fun cardsDao(): CardsDao
    abstract fun decksDao(): DecksDao
}