package com.example.beehive.data.users

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("SELECT * from users ORDER BY email ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * from users WHERE email LIKE :email")
    fun getUsersByEmail(email: String): Flow<List<User>>

    @Query("SELECT * from users WHERE id = :id")
    fun getUser(id: Int): Flow<User>

    @Query("SELECT COUNT(*) FROM passwords")
    suspend fun countUsers(): Int
}