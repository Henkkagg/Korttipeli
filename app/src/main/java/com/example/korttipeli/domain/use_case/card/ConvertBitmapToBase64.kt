package com.example.korttipeli.domain.use_case.card

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

class ConvertBitmapToBase64 @Inject constructor() {

    suspend operator fun invoke(bitmap: Bitmap): String {

        return withContext(Dispatchers.IO) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            Base64.getEncoder().encodeToString(byteArray)
        }
    }
}