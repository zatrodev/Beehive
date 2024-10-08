package com.example.beehive.data.credential

import kotlinx.coroutines.flow.Flow
import java.util.Date

class CredentialRepositoryImpl(private val credentialDao: CredentialDao) : CredentialRepository {
    override fun getCredentialStream(id: Int): Flow<Credential> = credentialDao.getCredential(id)

    override fun getAllCredentialsAndUser(): Flow<List<CredentialAndUser>> =
        credentialDao.getAllCredentialsAndUser()

    override fun getCredentialsByPackageNameStream(uri: String): Flow<List<Credential>> =
        credentialDao.getCredentialsByApp(uri)

    override fun getCredentialWithUser(id: Int): Flow<CredentialAndUser> =
        credentialDao.getCredentialWithUser(id)

    override fun getTrashedCredentials(): Flow<List<CredentialAndUser>> =
        credentialDao.getTrashedCredentials()

    override fun countTrashedCredentials(): Flow<Int> = credentialDao.countTrashedCredentials()

    override suspend fun restoreCredential(id: Int) = credentialDao.restoreCredential(id)

    override suspend fun trashCredential(id: Int, deletionDate: Date) =
        credentialDao.trashCredential(id, deletionDate)

    override suspend fun deleteAllTrashedCredentials() = credentialDao.deleteAllTrashedCredentials()

    override suspend fun deleteExpiredCredentials() = credentialDao.deleteExpiredCredentials()

    override suspend fun getNextId(): Int = credentialDao.getNextId()

    override suspend fun insertCredential(credential: Credential) = credentialDao.insert(credential)

    override suspend fun updateCredential(credential: Credential) = credentialDao.update(credential)

    override suspend fun deleteCredential(id: Int) = credentialDao.delete(id)

    override suspend fun updateDeletionDate(id: Int, deletionDate: Date) =
        credentialDao.updateDeletionDate(id, deletionDate)

}