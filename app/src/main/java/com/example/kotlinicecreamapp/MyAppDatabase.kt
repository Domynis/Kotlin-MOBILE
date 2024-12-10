package com.example.kotlinicecreamapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kotlinicecreamapp.core.data.OfflineChange
import com.example.kotlinicecreamapp.core.data.local.OfflineChangeDao
import com.example.kotlinicecreamapp.todo.data.IceCream
import com.example.kotlinicecreamapp.todo.data.local.IceCreamDao

@Database(entities = [IceCream::class, OfflineChange::class], version = 5)
abstract class MyAppDatabase : RoomDatabase() {
    abstract fun iceCreamDao() : IceCreamDao
    abstract fun offlineChangeDao() : OfflineChangeDao

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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}