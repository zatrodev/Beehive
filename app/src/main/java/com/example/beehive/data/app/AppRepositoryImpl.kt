package com.example.beehive.data.app

import kotlinx.coroutines.flow.Flow

class AppRepositoryImpl(
    private val appDao: AppDao,
) : AppRepository {
    override fun getAllInstalledApps(): Flow<List<AppInfo>> = appDao.getAllInstalledApps()

    override suspend fun getInstalledApps(packageNames: List<String>): List<AppInfo> =
        appDao.getInstalledApps(packageNames)

    override suspend fun insertAll(apps: List<AppInfo>) = appDao.insertAll(apps)

    override suspend fun insert(app: AppInfo) = appDao.insert(app)

    override suspend fun delete(app: AppInfo) = appDao.delete(app)
}