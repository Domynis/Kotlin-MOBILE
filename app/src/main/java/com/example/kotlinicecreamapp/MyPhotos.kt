package com.example.kotlinicecreamapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberImagePainter
import com.example.kotlinicecreamapp.camera.CameraCapture
import com.example.kotlinicecreamapp.gallery.EMPTY_IMAGE_URI
import com.example.kotlinicecreamapp.gallery.GallerySelect
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyPhotos(
    modifier: Modifier = Modifier,
    onImageUpdated: (String) -> Unit
) {
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    var showGallerySelect by remember { mutableStateOf(false) }

    if (imageUri != EMPTY_IMAGE_URI) {
        DisplayCapturedImage(
            imageUri = imageUri.toString(),
            onRemoveImage = {
                imageUri = EMPTY_IMAGE_URI
                onImageUpdated("")  // Reset image in parent
            },
            modifier = modifier
        )
    } else {
        if (showGallerySelect) {
            GallerySelect(
                modifier = modifier,
                onImageUri = { uri ->
                    showGallerySelect = false
                    updateImageUri(uri, onImageUpdated)
                }
            )
        } else {
            CameraOrGallerySelection(
                modifier = modifier,
                onImageCaptured = { file ->
                    updateImageUri(file.toUri(), onImageUpdated)
                },
                onSelectGallery = { showGallerySelect = true }
            )
        }
    }
}

@Composable
private fun DisplayCapturedImage(imageUri: String, onRemoveImage: () -> Unit, modifier: Modifier) {
    Box(modifier = modifier) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = rememberImagePainter(imageUri),
            contentDescription = "Captured image"
        )
        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onRemoveImage
        ) {
            Text("Remove image")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CameraOrGallerySelection(
    modifier: Modifier,
    onImageCaptured: (String) -> Unit,
    onSelectGallery: () -> Unit
) {
    Box(modifier = modifier) {
        CameraCapture(
            modifier = modifier,
            onImageCapture = onImageCaptured
        )
//        Button(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(4.dp),
//            onClick = onSelectGallery
//        ) {
//            Text("Select from Gallery")
//        }
    }
}

private fun updateImageUri(uri: android.net.Uri, onImageUpdated: (String) -> Unit) {
    val imageUriString = uri.toString()
    onImageUpdated(imageUriString)
}
