package com.example.beehive.data.user

import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override fun getAllUsersStream(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUserStream(id: Int): Flow<User> = userDao.getUser(id)

    override fun getUsersWithCredentials(): Flow<List<UserWithCredentials>> =
        userDao.getUsersWithCredentials()

    override suspend fun insertUser(user: User) = userDao.insert(user)

    override suspend fun getNextId(): Int = userDao.getNextId()
}