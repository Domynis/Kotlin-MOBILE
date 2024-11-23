package com.example.kotlinicecreamapp

import IceCreamRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kotlinicecreamapp.core.TAG
import com.example.kotlinicecreamapp.core.data.UserPreferences
import com.example.kotlinicecreamapp.core.data.UserPreferencesRepository
import kotlinx.coroutines.launch

class MyAppViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val iceCreamRepository: IceCreamRepository
) :
    ViewModel() {
    init {
        Log.d(TAG, "init")
    }

    fun logout() {
        viewModelScope.launch {
            iceCreamRepository.deleteAll()
            userPreferencesRepository.save(UserPreferences())
        }
    }
    fun setToken(token: String) {
        iceCreamRepository.setToken(token)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                MyAppViewModel(
                    app.container.userPreferencesRepository,
                    app.container.iceCreamRepository
                )
            }
        }
    }
}