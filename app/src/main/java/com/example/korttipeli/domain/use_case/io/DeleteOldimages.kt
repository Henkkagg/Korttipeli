package com.example.korttipeli.domain.use_case.io

import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.use_case.ImageType
import java.io.File
import javax.inject.Inject

class DeleteOldimages @Inject constructor(
    private val db: Database,
    private val directory: File
) {

    suspend operator fun invoke(imageType: ImageType) {
        val child = if (imageType is ImageType.Card) "cards" else "decks"
        val subDirectory = File(directory, child)

        val shouldHaveList = if (imageType is ImageType.Card) {
            db.cardsDao().getImageLocations().map { File(it) }
        } else db.decksDao().getImageLocations().map { File(it) }
        val shouldDeleteList = subDirectory.listFiles { file ->
            !shouldHaveList.contains(file)
        }
        shouldDeleteList?.forEach {
            it.delete()
        }
    }
}