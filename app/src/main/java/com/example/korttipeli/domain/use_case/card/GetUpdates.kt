package com.example.korttipeli.domain.use_case.card

import com.example.korttipeli.data.room.Database
import com.example.korttipeli.domain.model.Card
import com.example.korttipeli.domain.model.CardDataPackageIds
import com.example.korttipeli.domain.model.UpdatedImage
import com.example.korttipeli.domain.repository.CardsRepository
import com.example.korttipeli.domain.use_case.IOUsecases
import com.example.korttipeli.domain.use_case.ImageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class GetUpdates @Inject constructor(
    private val repository: CardsRepository,
    private val db: Database,
    private val directory: File,
    private val ioUsecases: IOUsecases
) {

    suspend operator fun invoke(): CardDataPackageIds {
        return withContext(Dispatchers.IO) {
            val cardIds = db.cardsDao().getAllIds()
            val cardDataPackage = repository.getAllCards(cardIds)

            //Delete cards
            cardDataPackage?.cardsToDelete?.let { db.cardsDao().deleteByIds(it) }

            //Add new cards
            val newCardsList = mutableListOf<Card>()
            cardDataPackage?.newCards?.forEach { card ->

                newCardsList.add(
                    card.copy(
                        image = ioUsecases.saveImage(
                            card.image,
                            card.idForImage,
                            ImageType.Card
                        )
                    )
                )
            }
            db.cardsDao().insertMany(newCardsList)

            //Update card infos
            cardDataPackage?.updatedInfos?.let { db.cardsDao().updateInfos(it) }

            //Update card images
            val updatedImagesWithPaths = mutableListOf<UpdatedImage>()
            cardDataPackage?.updatedImages?.forEach {
                updatedImagesWithPaths.add(
                    it.copy(
                        image = ioUsecases.saveImage(
                            it.image,
                            it.idForImage,
                            ImageType.Card
                        )
                    )
                )
            }
            db.cardsDao().updateImages(updatedImagesWithPaths)

            ioUsecases.deleteOldimages(ImageType.Card)

            val cardsToDeleteIds = cardDataPackage?.cardsToDelete ?: emptyList()
            val newCardIds = cardDataPackage?.newCards?.map { it.id } ?: emptyList()
            val updatedImageIds = updatedImagesWithPaths.map { it.id }
            val updatedInfoIds = cardDataPackage?.updatedInfos?.map { it.id } ?: emptyList()

            CardDataPackageIds(cardsToDeleteIds, newCardIds, updatedImageIds, updatedInfoIds)
        }
    }
}