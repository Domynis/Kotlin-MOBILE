package com.example.kotlinicecreamapp.todo.ui.iceCream

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.kotlinicecreamapp.MyPhotos
import com.example.kotlinicecreamapp.R
import com.example.kotlinicecreamapp.core.Result
import com.example.kotlinicecreamapp.sensor.LightSensor
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IceCreamScreen(iceCreamId: String?, onClose: () -> Unit) {
    val iceCreamViewModel =
        viewModel<IceCreamViewModel>(factory = IceCreamViewModel.Factory(iceCreamId))
    val iceCreamUiState = iceCreamViewModel.uiState

    var text by rememberSaveable { mutableStateOf(iceCreamUiState.iceCream?.name ?: "") }
    var tasty by rememberSaveable { mutableStateOf(iceCreamUiState.iceCream?.tasty ?: false) }
    var price by rememberSaveable { mutableDoubleStateOf(iceCreamUiState.iceCream.price) }
    var image by rememberSaveable { mutableStateOf(iceCreamUiState.iceCream?.image ?: "") }
    Log.d(
        "IceCreamScreen",
        "recompose, text = $text, tasty = $tasty, price = $price, image = ${image != ""}"
    )

    var isInitialized by remember { mutableStateOf(iceCreamId == null) }


    LaunchedEffect(iceCreamId, iceCreamUiState.loadResult) {
        if (!isInitialized && iceCreamUiState.loadResult !is Result.Loading) {
            text = iceCreamUiState.iceCream?.name ?: ""
            tasty = iceCreamUiState.iceCream?.tasty ?: false
            price = iceCreamUiState.iceCream.price
            image = iceCreamUiState.iceCream?.image ?: ""
            isInitialized = true
        }
    }

    // Handle submit result
    LaunchedEffect(iceCreamUiState.submitResult) {
        if (iceCreamUiState.submitResult is Result.Success) {
            onClose()
        }
    }
//    var doneInitialized by remember { mutableStateOf(iceCreamId == null) }
//    LaunchedEffect(iceCreamId, iceCreamUiState.loadResult) {
//        Log.d("IceCreamScreen", "Done initialized = ${iceCreamUiState.loadResult}")
//        if (doneInitialized) {
//            return@LaunchedEffect
//        }
//        if (!(iceCreamUiState.loadResult is Result.Loading)) {
//            done = iceCreamUiState.iceCream.done
//            doneInitialized = true
//        }
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.icecream)) },
                actions = {
                    Button(onClick = {
                        Log.d(
                            "IceCreamScreen",
                            "save iceCream text = $text, tasty = $tasty, price = $price, image = ${image != ""}"
                        )
                        iceCreamViewModel.saveOrUpdateIceCream(text, tasty, price, image)
                    }) { Text("Save") }
                }
            )
        }
    ) { padding ->
        IceCreamContent(
            uiState = iceCreamUiState,
            text = text,
            onTextChange = { text = it },
            tasty = tasty,
            onTastyChange = { tasty = it },
            price = price,
            onPriceChange = { price = it },
            image = image,
            onImageChange = { image = it },
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun IceCreamContent(
    uiState: IceCreamUiState,
    text: String,
    onTextChange: (String) -> Unit,
    tasty: Boolean,
    onTastyChange: (Boolean) -> Unit,
    price: Double,
    onPriceChange: (Double) -> Unit,
    image: String,
    onImageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPhotos by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        when (uiState.loadResult) {
            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is Result.Error -> {
                Text(
                    text = "Failed to load Ice Cream: ${(uiState.loadResult as Result.Error).exception?.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> {
                // Display the image if present
                if (image.isNotBlank()) {
                    Log.d("IceCreamScreen", "image = ${image != ""}")
                    val bytes = Base64.decode(image, Base64.DEFAULT)
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = android.graphics.Bitmap.createBitmap(
                                BitmapFactory.decodeByteArray(
                                    bytes,
                                    0,
                                    bytes.size
                                )
                            )
                        ),
                        contentDescription = "Ice Cream Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }



                if (showPhotos) {
                    MyPhotos(
                        modifier = Modifier.fillMaxWidth(),
                        onImageUpdated = { base64Image ->
                            onImageChange(base64Image)
                            showPhotos = false // Hide photo selector after choosing
                        }
                    )
                } else {
                    Button(
                        onClick = { showPhotos = true },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(if (image.isNotBlank()) "Change Photo" else "Add Photo")
                    }
                }

                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tasty:")
                    Switch(
                        checked = tasty,
                        onCheckedChange = onTastyChange,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                TextField(
                    value = price.toString(),
                    onValueChange = { onPriceChange(it.toDoubleOrNull() ?: 0.0) },
                    label = { Text("Price (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = price < 0
                )
                if (price < 0) {
                    Text(
                        text = "Price must be a positive value.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                LightSensor(modifier = Modifier.fillMaxSize())
            }
        }

        if (uiState.submitResult is Result.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else if (uiState.submitResult is Result.Error) {
            Text(
                text = "Failed to submit Ice Cream: ${(uiState.submitResult as Result.Error).exception?.message}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun PreviewIceCreamScreen() {
    IceCreamScreen(iceCreamId = "0", onClose = {})
}