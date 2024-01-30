package com.example.korttipeli.domain.use_case

import com.example.korttipeli.domain.use_case.io.DeleteOldimages
import com.example.korttipeli.domain.use_case.io.LoadImage
import com.example.korttipeli.domain.use_case.io.SaveImage
import javax.inject.Inject

data class IOUsecases @Inject constructor(
    val deleteOldimages: DeleteOldimages,
    val loadImage: LoadImage,
    val saveImage: SaveImage
)

sealed class ImageType {
    object Card : ImageType()
    object Deck : ImageType()
}