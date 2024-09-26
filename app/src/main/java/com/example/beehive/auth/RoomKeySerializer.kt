package com.example.beehive.auth

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

class RoomKeySerializer(
    private val cryptoManager: CryptoManager,
) : Serializer<String> {
    override val defaultValue: String
        get() = ""

    override suspend fun readFrom(input: InputStream): String {
        val decryptedBytes = cryptoManager.decrypt(input)
        return decryptedBytes.decodeToString()
    }

    override suspend fun writeTo(t: String, output: OutputStream) {
        cryptoManager.encrypt(t.encodeToByteArray(), output)
    }
}