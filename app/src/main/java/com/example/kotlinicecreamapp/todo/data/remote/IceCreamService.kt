package com.example.kotlinicecreamapp.todo.data.remote

import com.example.kotlinicecreamapp.todo.data.IceCream
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

//object IceCreamsService {
//    val iceCreams: MutableList<IceCream> = List(4) { index ->
//        IceCream(index, "Ice Cream $index", false)
//    }.toMutableList()
//
//    suspend fun getIceCreams(): List<IceCream> {
//        Log.d("IceCreamsService", "getIceCreams")
//        delay(5000)
//        return iceCreams.subList(0, 2)
//    }
//}

interface IceCreamService {
    @GET("/api/icecream")
    suspend fun find(@Header("Authorization") authorization: String): List<IceCream>

    @GET("api/icecream/{id}")
    suspend fun read(
        @Header("Authorization") authorization: String, @Path("id") id: String?
    ): IceCream

    @Headers("Content-Type: application/json")
    @PUT("api/icecream/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") id: String?,
        @Body iceCream: IceCream
    ): IceCream

    @Headers("Content-Type: application/json")
    @POST("api/icecream/")
    suspend fun create(
        @Header("Authorization") authorization: String,
        @Body iceCream: IceCream
    ): IceCream
}