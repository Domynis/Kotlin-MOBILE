package com.example.kotlinicecreamapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kotlinicecreamapp.todo.data.IceCream
import com.example.kotlinicecreamapp.todo.data.local.IceCreamDao

@Database(entities = [IceCream::class], version = 3)
abstract class MyAppDatabase : RoomDatabase() {
    abstract fun iceCreamDao() : IceCreamDao

    companion object {
        @Volatile
        private var INSTANCE: MyAppDatabase? = null

        fun getDatabase(context: Context): MyAppDatabase {
            val MIGRATION_2_3 = object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE IceCreams ADD COLUMN price REAL NOT NULL DEFAULT 0.0")
                    database.execSQL("ALTER TABLE IceCreams RENAME COLUMN text to name")
                    database.execSQL("ALTER TABLE IceCreams RENAME COLUMN done to tasty")
                }
            }
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