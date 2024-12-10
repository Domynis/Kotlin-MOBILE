package com.example.kotlinicecreamapp.todo

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.kotlinicecreamapp.AppContainer
import com.example.kotlinicecreamapp.core.OfflineSyncWorker

class IceCreamWorkerFactory(private val container: AppContainer) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            OfflineSyncWorker::class.java.name -> {
                OfflineSyncWorker(appContext, workerParameters)
            }

            else -> null
        }
    }
}