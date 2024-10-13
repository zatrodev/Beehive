package com.example.beehive.auth

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

class RoomKeySerializer(
    private val secretKeyManager: SecretKeyManager,
) : Serializer<String> {
    override val defaultValue: String
        get() = ""

    override suspend fun readFrom(input: InputStream): String {
        val decryptedBytes = secretKeyManager.decrypt(input)
        return decryptedBytes.decodeToString()
    }

    override suspend fun writeTo(t: String, output: OutputStream) {
        secretKeyManager.encrypt(t.encodeToByteArray(), output)
    }
}