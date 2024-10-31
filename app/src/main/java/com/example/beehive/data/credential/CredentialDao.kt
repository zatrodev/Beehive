package com.example.beehive.data.credential

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

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

    @Transaction
    @Query("SELECT * from credential WHERE id = :id")
    fun getCredentialAndUser(id: Int): Flow<CredentialAndUser>

    @Transaction
    @Query("SELECT * from credential WHERE deletionDate IS NULL")
    fun getAllCredentialsAndUser(): Flow<List<CredentialAndUser>>

    @Transaction
    @Query("SELECT * from credential WHERE packageName = :uri AND deletionDate IS NULL")
    fun getCredentialsByApp(uri: String): Flow<List<CredentialAndUser>>

    @Transaction
    @Query("SELECT * from credential WHERE deletionDate IS NOT NULL")
    fun getTrashedCredentials(): Flow<List<CredentialAndUser>>

    @Query("SELECT COUNT(*) from credential WHERE deletionDate IS NOT NULL")
    fun countTrashedCredentials(): Flow<Int>

    @Query("DELETE FROM credential WHERE deletionDate IS NOT NULL")
    suspend fun deleteAllTrashedCredentials()

    @Query("DELETE FROM credential WHERE deletionDate >= CURRENT_DATE")
    suspend fun deleteExpiredCredentials()

    @Query("UPDATE credential SET deletionDate = :deletedDate WHERE id = :id")
    suspend fun trashCredential(
        id: Int,
        deletedDate: Date,
    )

    @Query("UPDATE credential SET deletionDate = NULL WHERE id = :id")
    suspend fun restoreCredential(id: Int)

    @Query("SELECT MAX(id) from credential")
    suspend fun getCurrentId(): Int

    @Query("UPDATE credential SET deletionDate = :deletionDate WHERE id = :id")
    suspend fun updateDeletionDate(id: Int, deletionDate: Date)
}