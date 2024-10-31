package com.example.beehive.data.app

import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getAllInstalledApps(): Flow<List<AppInfo>>

    suspend fun getInstalledApps(packageNames: List<String>): List<AppInfo>

    suspend fun insert(app: AppInfo)

    suspend fun insertAll(apps: List<AppInfo>)

    suspend fun delete(app: AppInfo)
}