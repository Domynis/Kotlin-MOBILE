package com.example.kotlinicecreamapp.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "IceCreams")
data class IceCream(
    @PrimaryKey var _id: String = "",
    val name: String = "",
    val tasty: Boolean = false,
    val price: Double = 0.0) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}