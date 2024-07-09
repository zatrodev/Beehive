package com.example.beehive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Password::class], version = 1, exportSchema = false)
abstract class BeehiveDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao

    companion object {
        @Volatile
        private var Instance: BeehiveDatabase? = null

        fun getDatabase(context: Context): BeehiveDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, BeehiveDatabase::class.java, "beehive_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}