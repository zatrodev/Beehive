package com.example.beehive.data.passwords

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(password: Password)

    @Update
    suspend fun update(password: Password)

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * from passwords WHERE id = :id")
    fun getPassword(id: Int): Flow<Password>

    @Query("SELECT * from passwords WHERE uri = :uri")
    fun getPasswordsByUri(uri: String): Flow<List<Password>>

    @Query("SELECT * from passwords WHERE userId = :userId")
    fun getPasswordsByUserId(userId: Int): Flow<List<Password>>

    @Query("SELECT * from passwords ORDER BY name ASC")
    fun getAllPasswords(): Flow<List<Password>>

    @Query("SELECT MAX(id) from passwords")
    suspend fun countPasswords(): Int
}