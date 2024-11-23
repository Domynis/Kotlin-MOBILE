package com.example.kotlinicecreamapp.todo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinicecreamapp.todo.data.IceCream

@Database(entities = arrayOf(IceCream::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun iceCreamDao(): IceCreamDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}