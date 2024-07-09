package com.example.beehive.data

import kotlinx.coroutines.flow.Flow

interface PasswordsRepository {
    fun getAllPasswordsStream(): Flow<List<Password>>

    fun getPasswordStream(id: Int): Flow<Password>

    suspend fun insertPassword(password: Password)

    suspend fun updatePassword(password: Password)

    suspend fun deletePassword(password: Password)

    suspend fun countPasswords(): Int
}

