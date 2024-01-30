package com.example.korttipeli.domain.use_case.deck

import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.Deck
import com.example.korttipeli.domain.model.UpdatedImage
import com.example.korttipeli.domain.repository.DecksRepository
import com.example.korttipeli.domain.use_case.IOUsecases
import com.example.korttipeli.domain.use_case.ImageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoadAndUpdate @Inject constructor(
    private val db: Database,
    private val repository: DecksRepository,
    private val ioUsecases: IOUsecases,
    private val getTypesByIds: GetTypesByIds
) {

    suspend operator fun invoke() = flow {
        val cache = db.decksDao().getAll()
        emit(cache)

        val ids = cache.map { it.toIds() }
        val dataPackage = repository.getUpdates(ids)

        //Add new decks. Need to replace base64image with saved image location
        val newDecks = mutableListOf<Deck>()
        dataPackage.newDecks.forEach {
            val imagePath = ioUsecases.saveImage(it.image, it.idForImage, ImageType.Deck)
            newDecks.add(it.copy(image = imagePath))
        }
        db.decksDao().insertMany(newDecks)

        //Update deck images
        val updatedImagesWithPaths = mutableListOf<UpdatedImage>()
        dataPackage.updatedImages.forEach {
            val imagePath = ioUsecases.saveImage(it.image, it.idForImage, ImageType.Deck)
            updatedImagesWithPaths.add(it.copy(image = imagePath))
        }
        db.decksDao().updateImages(updatedImagesWithPaths)

        //Update other properties than images
        db.decksDao().updateNonImages(dataPackage.updatedNonImages)

        //Remove unauthorized decks and old images
        db.decksDao().deleteByIds(dataPackage.decksToDelete)
        ioUsecases.deleteOldimages(ImageType.Deck)

        val updated = db.decksDao().getAll()
        emit(updated)
    }.flowOn(Dispatchers.IO)
}