package com.example.beehive.data.users

import kotlinx.coroutines.flow.Flow

class UsersRepositoryImpl(private val userDao: UserDao) : UsersRepository {
    override fun getAllUsersStream(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUserStream(id: Int): Flow<User> = userDao.getUser(id)

    override fun getUsersByEmailStream(email: String): Flow<List<User>> =
        userDao.getUsersByEmail(email)

    override suspend fun insertUser(user: User) = userDao.insert(user)
}