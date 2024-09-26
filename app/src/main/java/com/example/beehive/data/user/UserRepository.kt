package com.example.beehive.data.user

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsersStream(): Flow<List<User>>

    fun getUserStream(id: Int): Flow<User>

    fun getUsersWithCredentials(): Flow<List<UserWithCredentials>>

    suspend fun insertUser(user: User)

    suspend fun getNextId(): Int
}