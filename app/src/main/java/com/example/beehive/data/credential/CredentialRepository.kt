package com.example.beehive.data.credential

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface CredentialRepository {
    fun getCredentialStream(id: Int): Flow<Credential>

    fun getCredentialsByPackageNameStream(uri: String): Flow<List<Credential>>

    fun getCredentialWithUser(id: Int): Flow<CredentialAndUser>

    fun getAllCredentialsAndUser(): Flow<List<CredentialAndUser>>

    fun getTrashedCredentials(): Flow<List<CredentialAndUser>>

    fun countTrashedCredentials(): Flow<Int>

    suspend fun restoreCredential(id: Int)

    suspend fun insertCredential(credential: Credential)

    suspend fun updateCredential(credential: Credential)

    suspend fun trashCredential(id: Int, deletionDate: Date)

    suspend fun deleteCredential(id: Int)

    suspend fun deleteAllTrashedCredentials()

    suspend fun deleteExpiredCredentials()

    suspend fun getNextId(): Int

    suspend fun updateDeletionDate(id: Int, deletionDate: Date)

}