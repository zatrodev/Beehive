package com.example.beehive.data.app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(app: AppInfo)

    @Delete
    suspend fun delete(app: AppInfo)

    @Query("SELECT * from installed_apps")
    fun getAllInstalledApps(): Flow<List<AppInfo>>

    @Query("SELECT * FROM installed_apps WHERE packageName IN (:packageNames)")
    suspend fun getInstalledApps(packageNames: List<String>): List<AppInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(apps: List<AppInfo>)
}