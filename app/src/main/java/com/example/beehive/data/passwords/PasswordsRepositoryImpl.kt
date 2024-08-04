package com.example.beehive.data.passwords

import kotlinx.coroutines.flow.Flow

class PasswordsRepositoryImpl(private val passwordDao: PasswordDao) : PasswordsRepository {
    override fun getAllPasswordsStream(): Flow<List<Password>> = passwordDao.getAllPasswords()

    override fun getPasswordStream(id: Int): Flow<Password> = passwordDao.getPassword(id)

    override fun getPasswordsByUriStream(uri: String): Flow<List<Password>> =
        passwordDao.getPasswordsByUri(uri)

    override fun getPasswordsByUserIdStream(userId: Int): Flow<List<Password>> =
        passwordDao.getPasswordsByUserId(userId)

    override suspend fun countPasswords(): Int = passwordDao.countPasswords()

    override suspend fun insertPassword(password: Password) = passwordDao.insert(password)

    override suspend fun updatePassword(password: Password) = passwordDao.update(password)

    override suspend fun deletePassword(id: Int) = passwordDao.delete(id)
}