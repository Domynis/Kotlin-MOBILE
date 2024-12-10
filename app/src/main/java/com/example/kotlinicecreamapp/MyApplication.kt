package com.example.kotlinicecreamapp

import android.app.Application
import android.util.Log
import com.example.kotlinicecreamapp.core.NotificationHelper
import com.example.kotlinicecreamapp.core.TAG
import com.example.kotlinicecreamapp.util.ConnectivityManagerNetworkMonitor

class MyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "init")
        val networkMonitor = ConnectivityManagerNetworkMonitor(this)
        val notificationHelper = NotificationHelper(this)
        container = AppContainer(this, networkMonitor, notificationHelper)
//        val configuration = Configuration.Builder()
//            .setWorkerFactory(IceCreamWorkerFactory(container))
//            .build()
//        Log.d(TAG, "Initializing WorkManager")
//        WorkManager.initialize(this, configuration)
    }
}