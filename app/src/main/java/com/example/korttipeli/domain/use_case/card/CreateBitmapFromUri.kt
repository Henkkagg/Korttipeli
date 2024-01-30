package com.example.korttipeli.domain.use_case.card

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CreateBitmapFromUri @Inject constructor(
    @ApplicationContext private val context: Context
) {

    operator fun invoke(uri: Uri): Bitmap {

        val cropLocation = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val contentResolver = context.contentResolver
        val source = ImageDecoder.createSource(contentResolver, uri)
        val unscaledBitmap = ImageDecoder.decodeBitmap(source)

        //Third party cropping library saves result to external storage which is not needed
        cropLocation?.listFiles()?.forEach { it.delete() }
        cropLocation?.delete()

        return Bitmap.createScaledBitmap(unscaledBitmap,720, 480, false)
    }
}