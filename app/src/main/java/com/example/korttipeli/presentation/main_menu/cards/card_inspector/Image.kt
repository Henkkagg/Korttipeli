package com.example.korttipeli.presentation.main_menu.cards.card_inspector

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
import com.example.korttipeli.R

@Composable
fun CardImageSectionEditor(
    viewModel: CardInspectorViewModel,
    generalShape: Shape,
    borderColor: Color,
    borderWidth: Dp,
    backPressed: () -> Unit = {}
) {
    val imageCropper =
        rememberLauncherForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                viewModel.onEvent(CardInspectorEvent.ImageChanged(result.uriContent!!))
            }
        }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        imageCropper.launch(
            options(uri) {
                setAspectRatio(3, 2)
                setImageSource(includeGallery = true, includeCamera = false)
                setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            }
        )
    }

    Image(
        painter = rememberAsyncImagePainter(
            model = if (viewModel.cardState.bitmap != null) viewModel.cardState.bitmap else R.drawable.placeholder_blue
        ),
        contentDescription = "Kortin kuva",
        modifier = Modifier
            .fillMaxWidth()
            .clip(generalShape)
            .border(borderWidth, borderColor, generalShape)
            .aspectRatio(1.5f)
            .clickable {
                if (viewModel.inspectorMode.editorEnabled) {
                    imagePicker.launch("image/*")
                } else backPressed()
            },
        contentScale = ContentScale.FillBounds
    )
}