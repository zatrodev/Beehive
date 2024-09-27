package com.example.beehive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beehive.auth.CryptoManager
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.CredentialDao
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserDao
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(entities = [Credential::class, User::class], version = 1, exportSchema = false)
abstract class BeehiveDatabase : RoomDatabase() {
    abstract fun passwordDao(): CredentialDao
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "beehive_database.db"

        @Volatile
        private var Instance: BeehiveDatabase? = null

        fun getDatabase(context: Context): BeehiveDatabase {
            System.loadLibrary("sqlcipher")

            val databaseFile = context.getDatabasePath(DATABASE_NAME)
            val factory =
                SupportOpenHelperFactory(CryptoManager.passphrase)

            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    BeehiveDatabase::class.java,
                    databaseFile.absolutePath
                )
                    .fallbackToDestructiveMigration()
                    .openHelperFactory(factory)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}