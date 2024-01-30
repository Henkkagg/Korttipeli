package com.example.korttipeli.domain.use_case

import com.example.korttipeli.domain.use_case.card.*
import javax.inject.Inject

data class CardUsecases @Inject constructor(
    val createBitmapFromUri: CreateBitmapFromUri,
    val convertBitmapToBase64: ConvertBitmapToBase64,
    val createNew: CreateNew,
    val preUploadImage: PreUploadImage,
    val getUpdates: GetUpdates,
    val loadByIds: LoadByIds,
    val loadUpdates: LoadUpdates,
    val loadOne: LoadOne,
    val update: Update,
    val deleteCard: DeleteCard,
    val sortBySetting: SortBySetting
)

sealed class CardResult {
    data class Success(
        val id: String,
        val idForImage: String = "",
        val idForOtherThanImage: String = "",
        val author: String = ""
    ) : CardResult()

    data class ImageTooLarge(val attemptedSizeInKb: Int, val allowedSizeInKb: Int) : CardResult()
    data class TitleTooLong(val attemptedLength: Int, val allowedLength: Int) : CardResult()
    data class DescriptionTooLong(val attemptedLength: Int, val allowedLength: Int) : CardResult()
    data class TitleAndDescriptionTooLong(
        val attemptedTitleLength: Int,
        val allowedTitleLength: Int,
        val attemptedDescriptionLength: Int,
        val allowedDescriptionLength: Int
    ) : CardResult()

    object ServerError : CardResult()
    object ClientError : CardResult()
}