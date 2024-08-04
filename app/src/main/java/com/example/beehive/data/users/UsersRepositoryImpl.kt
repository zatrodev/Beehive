package com.example.beehive.data.users

import kotlinx.coroutines.flow.Flow

class UsersRepositoryImpl(private val userDao: UserDao) : UsersRepository {
    override fun getAllUsersStream(): Flow<List<User>> = userDao.getAllUsers()

    override fun getUser(id: Int): Flow<User> = userDao.getUser(id)

    override suspend fun insertUser(user: User) = userDao.insert(user)
}