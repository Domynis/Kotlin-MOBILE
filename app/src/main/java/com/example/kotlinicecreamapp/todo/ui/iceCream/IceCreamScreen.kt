package com.example.kotlinicecreamapp.todo.ui.iceCream

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinicecreamapp.R
import com.example.kotlinicecreamapp.core.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IceCreamScreen(iceCreamId: String?, onClose: () -> Unit) {
    val iceCreamViewModel =
        viewModel<IceCreamViewModel>(factory = IceCreamViewModel.Factory(iceCreamId))
    val iceCreamUiState = iceCreamViewModel.uiState

    var text by rememberSaveable { mutableStateOf(iceCreamUiState.iceCream?.text ?: "") }
    var done by rememberSaveable { mutableStateOf(iceCreamUiState.iceCream?.done ?: false) }
    Log.d("IceCreamScreen", "recompose, text = $text, done = $done")

    var textInitialized by remember { mutableStateOf(iceCreamId == null) }


    LaunchedEffect(iceCreamId, iceCreamUiState.loadResult) {
        if (!textInitialized && iceCreamUiState.loadResult !is Result.Loading) {
            text = iceCreamUiState.iceCream?.text ?: ""
            done = iceCreamUiState.iceCream?.done ?: false
            textInitialized = true
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
                        Log.d("IceCreamScreen", "save iceCream text = $text")
                        iceCreamViewModel.saveOrUpdateIceCream(text)
                    }) { Text("Save") }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            if (iceCreamUiState.loadResult is Result.Loading) {
                CircularProgressIndicator()
                return@Column
            }
            if (iceCreamUiState.submitResult is Result.Loading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator()
                }
            }
            if (iceCreamUiState.loadResult is Result.Error) {
                Text(text = "Failed to load iceCream - ${(iceCreamUiState.loadResult as Result.Error).exception?.message}")
            }
            Row {
                TextField(
                    value = text,
                    onValueChange = { text = it }, label = { Text("Text") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (iceCreamUiState.submitResult is Result.Error) {
                Text(
                    text = "Failed to submit iceCream - ${(iceCreamUiState.submitResult as Result.Error).exception?.message}",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewIceCreamScreen() {
    IceCreamScreen(iceCreamId = "0", onClose = {})
}