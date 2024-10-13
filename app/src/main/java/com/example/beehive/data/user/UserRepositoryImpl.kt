package com.example.beehive.data.user

import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUser(id: Int): Flow<User> = userDao.getUser(id)

    override fun getUsersWithCredentials(): Flow<List<UserWithCredentials>> =
        userDao.getUsersWithCredentials()

    override suspend fun deleteUser(user: User) = userDao.delete(user)

    override suspend fun deleteById(id: Int) = userDao.deleteById(id)

    override suspend fun insertUser(user: User) = userDao.insert(user)

    override suspend fun getNextId(): Int = userDao.getNextId()
}