package com.example.kotlinicecreamapp.todo.ui.iceCream

import IceCreamRepository
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kotlinicecreamapp.MyApplication
import com.example.kotlinicecreamapp.todo.data.IceCream
import com.example.kotlinicecreamapp.core.Result
import com.example.kotlinicecreamapp.core.TAG
import kotlinx.coroutines.launch

data class IceCreamUiState(
    val iceCreamId: String? = null,
    val iceCream: IceCream = IceCream(),
    var loadResult: Result<IceCream>? = null,
    var submitResult: Result<IceCream>? = null,
)

class IceCreamViewModel(
    private val iceCreamId: String?,
    private val iceCreamRepository: IceCreamRepository
) : ViewModel() {

    var uiState: IceCreamUiState by mutableStateOf(IceCreamUiState(loadResult = Result.Loading))
        private set

    init {
        Log.d(TAG, "init")
        if (iceCreamId != null) {
            loadIceCream()
        } else {
            uiState = uiState.copy(loadResult = Result.Success(IceCream()))
        }
    }

    fun loadIceCream() {
        viewModelScope.launch {
            iceCreamRepository.iceCreamStream.collect { iceCreams ->
                if (!(uiState.loadResult is Result.Loading)) {
                    return@collect
                }
                val iceCream = iceCreams.find { it._id == iceCreamId } ?: IceCream()
                uiState = uiState.copy(iceCream = iceCream, loadResult = Result.Success(iceCream))
            }
        }
    }

    fun saveOrUpdateIceCream(text: String, tasty: Boolean, price: Double) {
        viewModelScope.launch {
            Log.d(TAG, "saveOrUpdateIceCream...");
            try {
                uiState = uiState.copy(submitResult = Result.Loading)
                val iceCream = uiState.iceCream.copy(name = text, tasty = tasty, price = price);
                val savedIceCream: IceCream = if (!iceCreamId.isNullOrEmpty()) {
                    iceCreamRepository.update(iceCream)
                } else {
                    iceCreamRepository.save(iceCream)
                }
                uiState = uiState.copy(
                    iceCream = savedIceCream,
                    submitResult = Result.Success(savedIceCream)
                )
            } catch (e: Exception) {
                Log.e(TAG, "saveOrUpdateIceCream failed", e)
                uiState = uiState.copy(submitResult = Result.Error(e))
            }
        }
    }

    companion object {
        fun Factory(iceCreamId: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                IceCreamViewModel(iceCreamId, app.container.iceCreamRepository)
            }
        }
    }
}
