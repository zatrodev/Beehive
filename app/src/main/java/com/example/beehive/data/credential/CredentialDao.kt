package com.example.beehive.data.credential

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(credential: Credential)

    @Update
    suspend fun update(credential: Credential)

    @Query("DELETE FROM credential WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * from credential WHERE id = :id")
    fun getCredential(id: Int): Flow<Credential>

    @Query("SELECT * from credential WHERE packageName = :uri")
    fun getCredentialsByApp(uri: String): Flow<List<Credential>>

    @Transaction
    @Query("SELECT * from credential WHERE id = :id")
    fun getCredentialWithUser(id: Int): Flow<CredentialWithUser>

    @Query("SELECT MAX(id) from credential")
    suspend fun getNextId(): Int
}