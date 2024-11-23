package com.example.kotlinicecreamapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinicecreamapp.todo.data.IceCream
import com.example.kotlinicecreamapp.todo.data.local.IceCreamDao

@Database(entities = [IceCream::class], version = 2)
abstract class MyAppDatabase : RoomDatabase() {
    abstract fun iceCreamDao() : IceCreamDao

    companion object {
        @Volatile
        private var INSTANCE: MyAppDatabase? = null

        fun getDatabase(context: Context): MyAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyAppDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}