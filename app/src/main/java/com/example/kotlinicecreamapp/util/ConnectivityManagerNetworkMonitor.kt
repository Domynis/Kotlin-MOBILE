package com.example.kotlinicecreamapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectivityManagerNetworkMonitor(val context: Context) {
    val isOnline: Flow<Boolean> = callbackFlow<Boolean> {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        if (connectivityManager == null) {
            channel.close(Throwable("No connectivity manager found"))
            return@callbackFlow
        }

        val callback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                channel.trySend(true).isSuccess
            }

            override fun onLost(network: Network) {
                channel.trySend(false).isSuccess
            }

            override fun onUnavailable() {
                channel.trySend(false).isSuccess
            }
        }

        var networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()



        connectivityManager.registerNetworkCallback(networkRequest, callback)

        val isCurrentlyOnline = connectivityManager.activeNetwork != null &&
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        channel.trySend(isCurrentlyOnline).isSuccess

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}