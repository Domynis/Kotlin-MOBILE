import android.util.Log
import com.example.kotlinicecreamapp.core.NotificationHelper
import com.example.kotlinicecreamapp.core.TAG
import com.example.kotlinicecreamapp.core.data.OfflineChange
import com.example.kotlinicecreamapp.core.data.local.OfflineChangeDao
import com.example.kotlinicecreamapp.core.data.remote.Api
import com.example.kotlinicecreamapp.todo.data.IceCream
import com.example.kotlinicecreamapp.todo.data.local.IceCreamDao
import com.example.kotlinicecreamapp.todo.data.remote.IceCreamEvent
import com.example.kotlinicecreamapp.todo.data.remote.IceCreamService
import com.example.kotlinicecreamapp.todo.data.remote.IceCreamWsClient
import com.example.kotlinicecreamapp.util.ConnectivityManagerNetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class IceCreamRepository(
    private val iceCreamService: IceCreamService,
    private val iceCreamWsClient: IceCreamWsClient,
    private val iceCreamDao: IceCreamDao,
    val offlineChangesDao: OfflineChangeDao,
    private val networkMonitor: ConnectivityManagerNetworkMonitor,
    private val notificationHelper: NotificationHelper
) {
    val iceCreamStream by lazy { iceCreamDao.getAll() }
    val offlineChangesStream by lazy { offlineChangesDao.getAllChanges() }

    init {
        Log.d(TAG, "init")
    }

    private fun getBearerToken() = "Bearer ${Api.tokenInterceptor.token}"

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            val iceCreams = iceCreamService.find(authorization = getBearerToken())
            iceCreamDao.deleteAll()
            iceCreams.forEach { iceCreamDao.insert(it) }
            Log.d(TAG, "refresh succeeded")
        } catch (e: Exception) {
            Log.w(TAG, "refresh failed", e)
        }
    }

    suspend fun openWsClient() {
        Log.d(TAG, "openWsClient")
        withContext(Dispatchers.IO) {
            getIceCreamEvents().collect {
                Log.d(TAG, "IceCream event collected $it")
                if (it.isSuccess) {
                    val iceCreamEvent = it.getOrNull()
                    when (iceCreamEvent?.type) {
                        "created" -> handleIceCreamCreated(iceCreamEvent.payload)
                        "updated" -> handleIceCreamUpdated(iceCreamEvent.payload)
                        "deleted" -> handleIceCreamDeleted(iceCreamEvent.payload)
                    }
                }
            }
        }
    }

    suspend fun closeWsClient() {
        Log.d(TAG, "closeWsClient")
        withContext(Dispatchers.IO) {
            iceCreamWsClient.closeSocket()
        }
    }

    suspend fun getIceCreamEvents(): Flow<Result<IceCreamEvent>> = callbackFlow {
        Log.d(TAG, "getIceCreamEvents started")
        iceCreamWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
            },
            onClosed = {
                Log.d(TAG, "onClosed")
                close()
            },
            onFailure = {
                Log.d(TAG, "onFailure")
                close()
            }
        )
        awaitClose { iceCreamWsClient.closeSocket() }
    }

    suspend fun updateOnline(iceCream: IceCream): IceCream {
        Log.d(TAG, "updateOnline $iceCream...")
        return iceCreamService.update(
            iceCream = iceCream,
            id = iceCream._id,
            authorization = getBearerToken()
        )
    }

    suspend fun update(iceCream: IceCream): IceCream {
        Log.d(TAG, "update $iceCream...")
        return try {
            val isOnline = networkMonitor.isOnline.first()
            if (isOnline) {
                val updatedIceCream = updateOnline(iceCream)
                Log.d(TAG, "update $iceCream succeeded online")
                handleIceCreamUpdated(updatedIceCream)
                updatedIceCream
            } else {
                Log.d(TAG, "Network is offline, saving $iceCream locally")
                notificationHelper.showUpdateFailedNotification(iceCream)
                offlineChangesDao.insert(
                    OfflineChange(
                        type = "update",
                        iceCreamId = iceCream._id,
                        iceCreamJson = iceCream.toJson()
                    )
                )
                handleIceCreamUpdated(iceCream)
                iceCream
            }
        } catch (e: Exception) {
            Log.d(TAG, "update $iceCream failed")
            notificationHelper.showUpdateFailedNotification(iceCream)
            offlineChangesDao.insert(
                OfflineChange(
                    type = "update",
                    iceCreamId = iceCream._id,
                    iceCreamJson = iceCream.toJson()
                )
            )
            handleIceCreamUpdated(iceCream)
            iceCream
        }
    }

    suspend fun saveOnline(iceCream: IceCream): IceCream {
        Log.d(TAG, "saveOnline $iceCream...")
        return iceCreamService.create(
            iceCream = iceCream,
            authorization = getBearerToken()
        )
    }

    suspend fun save(iceCream: IceCream): IceCream {
        Log.d(TAG, "save $iceCream...")

        return try {
            val isOnline = networkMonitor.isOnline.first()
            if (isOnline) {
                val savedIceCream = saveOnline(iceCream)
                Log.d(TAG, "save $iceCream succeeded online")
                handleIceCreamCreated(savedIceCream)
                savedIceCream
            } else {
                Log.d(TAG, "Network is offline, saving $iceCream locally")
                notificationHelper.showCreateFailedNotification(iceCream)
                // Assign a temporary ID for offline use
                if (iceCream._id.isEmpty()) {
                    iceCream._id = "local-${UUID.randomUUID()}"
                }

                offlineChangesDao.insert(
                    OfflineChange(
                        type = "create",
                        iceCreamId = iceCream._id,
                        iceCreamJson = iceCream.toJson()
                    )
                )
                handleIceCreamCreated(iceCream)
                iceCream
            }
        } catch (e: Exception) {
            Log.d(TAG, "save $iceCream failed")
            notificationHelper.showCreateFailedNotification(iceCream)

            if (iceCream._id.isEmpty()) {
                iceCream._id = "local-${UUID.randomUUID()}"
            }

            offlineChangesDao.insert(
                OfflineChange(
                    type = "create",
                    iceCreamId = iceCream._id,
                    iceCreamJson = iceCream.toJson()
                )
            )
            handleIceCreamCreated(iceCream)
            iceCream
        }
    }

    private suspend fun handleIceCreamDeleted(iceCream: IceCream) {
        Log.d(TAG, "handleIceCreamDeleted $iceCream")
        //TODO: Implement delete
    }

    suspend fun handleIceCreamUpdated(iceCream: IceCream) {
        Log.d(TAG, "handleIceCreamUpdated $iceCream")
        iceCreamDao.update(iceCream)
    }

    private suspend fun handleIceCreamCreated(iceCream: IceCream) {
        Log.d(TAG, "handleIceCreamCreated $iceCream")
        iceCreamDao.insert(iceCream)
    }

    suspend fun deleteAll() {
        Log.d(TAG, "deleteAll")
        iceCreamDao.deleteAll()
    }

    fun setToken(token: String) {
        Log.d(TAG, "setToken $token")
        iceCreamWsClient.authorize(token)
    }
}