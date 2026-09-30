package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room SQLite Database for offline caching of religious rulings and fatwas.
 */
@Database(
    entities = [FatwaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FatwaRoomDatabase : RoomDatabase() {
    abstract fun fatwaDao(): FatwaDao

    companion object {
        @Volatile
        private var INSTANCE: FatwaRoomDatabase? = null

        fun getDatabase(context: Context): FatwaRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FatwaRoomDatabase::class.java,
                    "sistani_fatwas_offline.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
