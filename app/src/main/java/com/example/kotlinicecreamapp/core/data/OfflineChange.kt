package com.example.kotlinicecreamapp.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kotlinicecreamapp.todo.data.IceCream

@Entity(tableName = "OfflineChanges")
data class OfflineChange(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // e.g., "create", "update", "delete"
    val iceCreamId: String,
    val iceCreamJson: String
)