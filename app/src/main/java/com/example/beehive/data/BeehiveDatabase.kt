package com.example.beehive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.CredentialDao
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserDao

@Database(entities = [Credential::class, User::class], version = 6, exportSchema = false)
abstract class BeehiveDatabase : RoomDatabase() {
    abstract fun passwordDao(): CredentialDao
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