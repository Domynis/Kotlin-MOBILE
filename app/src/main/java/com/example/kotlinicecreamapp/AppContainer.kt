package com.example.kotlinicecreamapp

import IceCreamRepository
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import com.example.kotlinicecreamapp.auth.data.AuthRepository
import com.example.kotlinicecreamapp.auth.data.remote.AuthDataSource
import com.example.kotlinicecreamapp.core.TAG
import com.example.kotlinicecreamapp.core.data.UserPreferencesRepository
import com.example.kotlinicecreamapp.core.data.remote.Api
import com.example.kotlinicecreamapp.todo.data.remote.IceCreamService
import com.example.kotlinicecreamapp.todo.data.remote.IceCreamWsClient
import com.example.kotlinicecreamapp.util.ConnectivityManagerNetworkMonitor


val Context.userPreferencesDataStore by preferencesDataStore(
    name = "user_preferences"
)

class AppContainer(val context: Context, val networkMonitor: ConnectivityManagerNetworkMonitor) {
    init {
        Log.d(TAG, "init")
    }

    private val iceCreamService: IceCreamService =
        Api.retrofit.create(IceCreamService::class.java)
    private val iceCreamsWsClient: IceCreamWsClient = IceCreamWsClient(Api.okHttpClient)
    private val authDataSource: AuthDataSource = AuthDataSource()

    private val database: MyAppDatabase by lazy { MyAppDatabase.getDatabase(context) }

    val iceCreamRepository : IceCreamRepository by lazy {
        IceCreamRepository(iceCreamService, iceCreamsWsClient, database.iceCreamDao(), database.offlineChangeDao(), networkMonitor)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(authDataSource)
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.userPreferencesDataStore)
    }
}