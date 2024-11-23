package com.example.kotlinicecreamapp.todo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kotlinicecreamapp.todo.data.IceCream
import kotlinx.coroutines.flow.Flow

@Dao
interface IceCreamDao {
    @Query("SELECT * FROM IceCreams")
    fun getAll(): Flow<List<IceCream>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(iceCream: IceCream)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(iceCreams: List<IceCream>)

    @Update
    suspend fun update(iceCream: IceCream): Int

    @Query("DELETE FROM IceCreams WHERE _id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM IceCreams")
    suspend fun deleteAll()
}