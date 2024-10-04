package com.example.beehive.data

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.credential.CredentialRepositoryImpl
import com.example.beehive.data.user.UserRepository
import com.example.beehive.data.user.UserRepositoryImpl

interface BeehiveContainer {
    val credentialRepository: CredentialRepository
    val userRepository: UserRepository
    val packageManager: PackageManager
    val dataStore: DataStore<Preferences>
}

class BeehiveContainerImpl(private val applicationContext: Context) : BeehiveContainer {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "settings"
    )

    override val credentialRepository: CredentialRepository by lazy {
        CredentialRepositoryImpl(BeehiveDatabase.getDatabase(applicationContext).passwordDao())
    }

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(BeehiveDatabase.getDatabase(applicationContext).userDao())
    }

    override val packageManager: PackageManager
        get() = applicationContext.packageManager

    override val dataStore: DataStore<Preferences>
        get() = applicationContext.dataStore
}