package com.example.kotlinicecreamapp.core.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    private val url = "192.168.100.39"
    private val httpUrl = "http://$url:3000/"

    val wsUrl = "ws://$url:3000/"

    private var gson = GsonBuilder().create()

    val tokenInterceptor = TokenInterceptor()

    val okHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(tokenInterceptor)
    }.build()

    val retrofit = Retrofit.Builder()
        .baseUrl(httpUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
//        .client(okHttpClient)
        .build()
}