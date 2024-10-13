package com.example.beehive.data.user

import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAllUsers(): Flow<List<User>>

    fun getUser(id: Int): Flow<User>

    fun getUsersWithCredentials(): Flow<List<UserWithCredentials>>

    suspend fun insertUser(user: User)

    suspend fun deleteUser(user: User)

    suspend fun deleteById(id: Int)

    suspend fun getNextId(): Int
}