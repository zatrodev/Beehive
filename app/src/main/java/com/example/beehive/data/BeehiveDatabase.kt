package com.example.beehive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordDao
import com.example.beehive.data.users.User
import com.example.beehive.data.users.UserDao

@Database(entities = [Password::class, User::class], version = 5, exportSchema = false)
abstract class BeehiveDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
    abstract fun userDao(): UserDao

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