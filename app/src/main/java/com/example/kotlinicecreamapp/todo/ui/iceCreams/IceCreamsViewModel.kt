package com.example.kotlinicecreamapp.todo.ui.iceCreams

import IceCreamRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kotlinicecreamapp.MyApplication
import com.example.kotlinicecreamapp.core.TAG
import com.example.kotlinicecreamapp.todo.data.IceCream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

data class IceCreamsUiState(val iceCreams: List<IceCream>)

class IceCreamsViewModel(private val iceCreamRepository: IceCreamRepository) : ViewModel() {
    val uiState: Flow<List<IceCream>> = iceCreamRepository.iceCreamStream

    init {
        Log.d(TAG, "init")
        loadItems()
    }

    fun loadItems() {
        Log.d(TAG, "loadItems...")
        viewModelScope.launch {
            iceCreamRepository.refresh()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                IceCreamsViewModel(app.container.iceCreamRepository)
            }
        }
    }
}