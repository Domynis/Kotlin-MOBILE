package com.example.kotlinicecreamapp.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "IceCreams")
data class IceCream(
    @PrimaryKey val _id: String = "",
    val name: String = "",
    val tasty: Boolean = false,
    val price: Double = 0.0)