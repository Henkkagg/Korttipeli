package com.example.korttipeli.domain.use_case.io

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import javax.inject.Inject

class LoadImage @Inject constructor() {

    operator fun invoke(fileLocation: String): Bitmap {

        return BitmapFactory.decodeFile(fileLocation)
    }
}