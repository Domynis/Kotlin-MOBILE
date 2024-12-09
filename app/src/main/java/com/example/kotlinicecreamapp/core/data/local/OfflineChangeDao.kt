package com.example.kotlinicecreamapp.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query
import com.example.kotlinicecreamapp.core.data.OfflineChange
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineChangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(change: OfflineChange)

    @Query("SELECT * FROM OfflineChanges")
    fun getAllChanges(): Flow<List<OfflineChange>>

    @Delete
    suspend fun delete(change: OfflineChange)

    @Query("DELETE FROM OfflineChanges")
    suspend fun clearAll()
}

