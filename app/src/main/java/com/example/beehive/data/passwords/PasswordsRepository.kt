package com.example.beehive.data.passwords

import kotlinx.coroutines.flow.Flow

interface PasswordsRepository {
    fun getAllPasswordsStream(): Flow<List<Password>>

    fun getPasswordStream(id: Int): Flow<Password>

    fun getPasswordsByUri(uri: String): Flow<List<Password>>

    suspend fun insertPassword(password: Password)

    suspend fun updatePassword(password: Password)

    suspend fun deletePassword(id: Int)

    suspend fun countPasswords(): Int
}

