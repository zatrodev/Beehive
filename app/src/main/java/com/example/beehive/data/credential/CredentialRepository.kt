package com.example.beehive.data.credential

import kotlinx.coroutines.flow.Flow

interface CredentialRepository {
    fun getCredentialStream(id: Int): Flow<Credential>

    fun getCredentialsByPackageNameStream(uri: String): Flow<List<Credential>>

    fun getCredentialWithUser(id: Int): Flow<CredentialWithUser>

    suspend fun insertCredential(credential: Credential)

    suspend fun updateCredential(credential: Credential)

    suspend fun deleteCredential(id: Int)

    suspend fun getNextId(): Int
}