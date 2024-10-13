package com.example.beehive.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.beehive.auth.RoomKeySerializer
import com.example.beehive.auth.SecretKeyManager

interface AuthContainer {
    val dataStore: DataStore<String>
}

private val Context.dataStore by dataStore(
    fileName = "room-key",
    serializer = RoomKeySerializer(SecretKeyManager())
)

class AuthContainerImpl(applicationContext: Context) : AuthContainer {
    override val dataStore: DataStore<String> = applicationContext.dataStore
}