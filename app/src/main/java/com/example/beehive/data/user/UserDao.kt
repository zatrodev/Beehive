package com.example.beehive.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("SELECT * from user ORDER BY email ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * from user WHERE id = :id")
    fun getUser(id: Int): Flow<User>

    @Transaction
    @Query("SELECT * FROM user ORDER BY email ASC")
    fun getUsersWithCredentials(): Flow<List<UserWithCredentials>>

    @Query("SELECT MAX(id) FROM user")
    suspend fun getNextId(): Int
}