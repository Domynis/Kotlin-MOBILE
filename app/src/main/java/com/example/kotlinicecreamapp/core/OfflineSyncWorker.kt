package com.example.kotlinicecreamapp.core

import IceCreamRepository
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kotlinicecreamapp.MyApplication
import com.example.kotlinicecreamapp.core.data.OfflineChange
import com.example.kotlinicecreamapp.todo.data.IceCream
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach

class OfflineSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val iceCreamRepository: IceCreamRepository by lazy {
        // Obtain repository from your dependency injection or app container
        (applicationContext as MyApplication).container.iceCreamRepository
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "OfflineSyncWorker doWork")

        try {
            // Keep fetching and processing the first change until no changes remain
            while (true) {
                // Fetch the latest first offline change, or exit if no changes exist
                val change = iceCreamRepository.offlineChangesStream.first().firstOrNull() ?: break

                try {
                    when (change.type) {
                        "create" -> handleCreateChange(change)
                        "update" -> handleUpdateChange(change)
                    }

                    // Delete the processed change from the database
                    iceCreamRepository.offlineChangesDao.delete(change)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync offline change: $change", e)
                    return Result.retry() // Retry the worker if an exception occurs
                }
            }

            iceCreamRepository.refresh() // Refresh data after syncing changes
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to complete offline sync", e)
            return Result.retry()
        }
    }

    private suspend fun handleCreateChange(change: OfflineChange) {
        Log.d(TAG, "Processing offline create change: $change")
        val localIceCream = Gson().fromJson(change.iceCreamJson, IceCream::class.java)
        val syncedIceCream = iceCreamRepository.saveOnline(localIceCream)
        Log.d(TAG, "Synced IceCream: $syncedIceCream (was $localIceCream)")

        // Update local state with the new ID
        iceCreamRepository.handleIceCreamUpdated(syncedIceCream)

        // Update remaining offline changes related to this IceCream with the new ID
        val updatesToSync = iceCreamRepository.offlineChangesDao
            .getChangesByIceCreamId(localIceCream._id)
            .first()

        Log.d(TAG, "Updating ${updatesToSync.size} changes with new IceCream ID")
        updatesToSync.forEach { offlineChange ->
            val newChange = offlineChange.copy(
                iceCreamId = syncedIceCream._id,
                iceCreamJson = offlineChange.iceCreamJson.replace(
                    localIceCream._id,
                    syncedIceCream._id
                )
            )
            iceCreamRepository.offlineChangesDao.update(newChange)
        }
    }

    private suspend fun handleUpdateChange(change: OfflineChange) {
        Log.d(TAG, "Processing offline update change: $change")
        val updatedIceCream = Gson().fromJson(change.iceCreamJson, IceCream::class.java)
        iceCreamRepository.updateOnline(updatedIceCream)
    }
}