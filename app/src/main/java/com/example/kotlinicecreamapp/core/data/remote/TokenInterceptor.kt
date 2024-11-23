package com.example.kotlinicecreamapp.core.data.remote

import android.util.Log
import com.example.kotlinicecreamapp.core.TAG
import okhttp3.Interceptor

class TokenInterceptor : Interceptor {
    var token: String? = null

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original = chain.request()
        val originalUrl = original.url
        if (token == null) {
            return chain.proceed(original)
        }
        val requestBuilder = original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .url(originalUrl)
        val request = requestBuilder.build()
        Log.d(TAG, "Authorization bearer added")
        return chain.proceed(request)
    }
}