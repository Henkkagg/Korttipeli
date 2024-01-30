package com.example.korttipeli.domain.use_case.io

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.korttipeli.domain.use_case.ImageType
import java.io.File
import java.util.*
import javax.inject.Inject

class SaveImage @Inject constructor(
    private val directory: File
) {

    suspend operator fun invoke(
        base64Image: String,
        fileName: String,
        imageType: ImageType
    ): String {

        val subDirectory = if (imageType is ImageType.Card) {
            File(directory, "cards")
        } else File(directory, "decks")
        subDirectory.mkdir()
        val imagePath = File(subDirectory, fileName)

        val byteArray = Base64.getDecoder().decode(base64Image)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        imagePath.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        return imagePath.toString()
    }
}