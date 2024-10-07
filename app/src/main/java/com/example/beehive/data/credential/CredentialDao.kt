package com.example.beehive.data.credential

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
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

    @Query("SELECT * from credential WHERE deletionDate IS NULL")
    fun getAllCredentialsAndUser(): Flow<List<CredentialAndUser>>

    @Query("SELECT * from credential WHERE packageName = :uri")
    fun getCredentialsByApp(uri: String): Flow<List<Credential>>

    @Query("SELECT * from credential WHERE deletionDate IS NOT NULL")
    fun getTrashedCredentials(): Flow<List<CredentialAndUser>>

    @Query("DELETE FROM credential WHERE deletionDate IS NOT NULL")
    suspend fun deleteAllTrashedCredentials()

    @Query("DELETE FROM credential WHERE deletionDate >= CURRENT_DATE")
    suspend fun deleteExpiredCredentials()

    @Query("UPDATE credential SET deletionDate = :deletedDate WHERE id = :id")
    suspend fun trashCredential(
        id: Int,
        deletedDate: Date = Date.from(
            LocalDate.now().plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant()
        ),
    )

    @Query("UPDATE credential SET deletionDate = NULL WHERE id = :id")
    suspend fun restoreCredential(id: Int)

    @Transaction
    @Query("SELECT * from credential WHERE id = :id")
    fun getCredentialWithUser(id: Int): Flow<CredentialAndUser>

    @Query("SELECT MAX(id) from credential")
    suspend fun getNextId(): Int
}