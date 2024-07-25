package com.example.beehive.data.users

import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getALlUsers(): Flow<List<User>>

    fun getUser(id: Int): Flow<User>

    suspend fun insertUser(user: User)
}