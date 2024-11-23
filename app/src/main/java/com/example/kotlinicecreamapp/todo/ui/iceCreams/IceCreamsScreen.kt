package com.example.kotlinicecreamapp.todo.ui.iceCreams

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinicecreamapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IceCreamsScreen(
    onIceCreamClick: (id: String?) -> Unit,
    onAddItem: () -> Unit,
    onLogout: () -> Unit
) {
    Log.d("IceCreamsScreen", "recompose")
    val iceCreamsViewModel = viewModel<IceCreamsViewModel>(factory = IceCreamsViewModel.Factory)
    val iceCreamsUiState by iceCreamsViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = listOf()
    )
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
            FloatingActionButton(
                onClick = {
                    Log.d("IceCreamsScreen", "add")
                    onAddItem()
                },
            ) { Icon(Icons.Rounded.Add, "Add") }
        }
    ) {
        IceCreamList(
            iceCreams = iceCreamsUiState,
            onIceCreamClick = onIceCreamClick,
            modifier = Modifier.padding(it)
        )
    }
}

@Preview
@Composable
fun PreviewTodoScreen() {
    IceCreamsScreen(onIceCreamClick = {}, onAddItem = {}, onLogout = {})
}