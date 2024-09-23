package com.example.beehive.data.credential

import kotlinx.coroutines.flow.Flow

class CredentialRepositoryImpl(private val credentialDao: CredentialDao) : CredentialRepository {
    override fun getCredentialStream(id: Int): Flow<Credential> = credentialDao.getCredential(id)

    override fun getCredentialsByPackageNameStream(uri: String): Flow<List<Credential>> =
        credentialDao.getCredentialsByApp(uri)

    override fun getCredentialWithUser(id: Int): Flow<CredentialWithUser> =
        credentialDao.getCredentialWithUser(id)

    override suspend fun getNextId(): Int = credentialDao.getNextId()

    override suspend fun insertCredential(credential: Credential) = credentialDao.insert(credential)

    override suspend fun updateCredential(credential: Credential) = credentialDao.update(credential)

    override suspend fun deleteCredential(id: Int) = credentialDao.delete(id)
}