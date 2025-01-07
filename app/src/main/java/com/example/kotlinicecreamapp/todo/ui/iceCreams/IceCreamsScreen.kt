package com.example.kotlinicecreamapp.todo.ui.iceCreams

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinicecreamapp.R
import com.example.kotlinicecreamapp.animations.MyFloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IceCreamsScreen(
    onIceCreamClick: (id: String?) -> Unit,
    onAddItem: () -> Unit,
    onLogout: () -> Unit
) {
    var isAdding by remember { mutableStateOf(false) }
    var isListVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    suspend fun showAddMessage() {
        if (!isAdding) {
            isAdding = true
            delay(600L)
            isAdding = false
        }
    }

    Log.d("IceCreamsScreen", "recompose")
    val iceCreamsViewModel = viewModel<IceCreamsViewModel>(factory = IceCreamsViewModel.Factory)
    val iceCreamsUiState by iceCreamsViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = listOf()
    )

    // Animate the list's visibility when data is loaded
    LaunchedEffect(iceCreamsUiState) {
        isListVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.icecreams)) },
                actions = {
                    Button(onClick = onLogout) { Text("Logout") }
                }
            )
        },
        floatingActionButton = {
            MyFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        showAddMessage()
                        Log.d("IceCreamsScreen", "add")
                        onAddItem()
                    }
                },
                isAdding = isAdding
            )
        }
    ) {
        AnimatedVisibility(
            visible = isListVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 2000))
        ) {
            IceCreamList(
                iceCreams = iceCreamsUiState,
                onIceCreamClick = onIceCreamClick,
                modifier = Modifier.padding(it)
            )
        }
    }
}

@Preview
@Composable
fun PreviewTodoScreen() {
    IceCreamsScreen(onIceCreamClick = {}, onAddItem = {}, onLogout = {})
}