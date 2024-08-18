package com.example.beehive.data.users

import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getAllUsersStream(): Flow<List<User>>

    fun getUserStream(id: Int): Flow<User>

    fun getUsersByEmailStream(email: String): Flow<List<User>>

    suspend fun insertUser(user: User)
}