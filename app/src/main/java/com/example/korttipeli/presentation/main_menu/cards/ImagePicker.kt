package com.example.korttipeli.presentation.main_menu.cards

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options

//Opens image picker, asks to crop the chosen image, returns the cropped Uri via onImageCropped
@Composable
fun ImagePicker(
    onImagePicked: (Uri?) -> Unit
) {
    val imageCropper = rememberLauncherForActivityResult(CropImageContract()) { result ->
            onImagePicked(result.uriContent)
        }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { originalUri ->
        if (originalUri == null) {
            onImagePicked(null)
            return@rememberLauncherForActivityResult
        }
        imageCropper.launch(
            options(originalUri) {
                setAspectRatio(3, 2)
                setImageSource(includeGallery = true, includeCamera = false)
                setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            }
        )
    }

    LaunchedEffect(Unit) {
        imagePicker.launch("image/*")
    }
}