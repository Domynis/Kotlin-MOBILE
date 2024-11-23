package com.example.kotlinicecreamapp.todo.data.remote

import android.util.Log
import com.example.kotlinicecreamapp.core.TAG
import com.example.kotlinicecreamapp.core.data.remote.Api
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class IceCreamWsClient(private val okHttpClient: OkHttpClient) {
    lateinit var webSocket: WebSocket

    suspend fun openSocket(
        onEvent: (iceCreamEvent: IceCreamEvent?) -> Unit,
        onClosed: () -> Unit,
        onFailure: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "openSocket")
            val request = Request.Builder().url(Api.wsUrl).build()
            webSocket = okHttpClient.newWebSocket(
                request, IceCreamWebSocketListener(
                    onEvent = onEvent, onClosed = onClosed, onFailure = onFailure
                )
            )
            okHttpClient.dispatcher.executorService.shutdown()
        }
    }

    fun closeSocket() {
        Log.d(TAG, "closeSocket")
        webSocket.close(1000, "")
    }

    inner class IceCreamWebSocketListener(
        private val onEvent: (iceCreamEvent: IceCreamEvent?) -> Unit,
        private val onClosed: () -> Unit,
        private val onFailure: () -> Unit
    ) : WebSocketListener() {
        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
            .build()
        private val iceCreamEventJsonAdapter: JsonAdapter<IceCreamEvent> =
            moshi.adapter(IceCreamEvent::class.java)

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "onMessage string $text")
            val iceCreamEvent = iceCreamEventJsonAdapter.fromJson(text)
            onEvent(iceCreamEvent)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "onMessage bytes $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "onClosed $code $reason")
            onClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d(TAG, "onFailure $t")
            onFailure()
        }
    }

    fun authorize(token: String) {
        val auth = """
            {
              "type":"authorization",
              "payload":{
                "token": "$token"
              }
            }
        """.trimIndent()
        Log.d(TAG, "auth $auth")
        if(this::webSocket.isInitialized) {
            webSocket.send(auth)
        } else {
            Log.d(TAG, "webSocket not initialized")
        }
    }
}