package com.example.beehive.domain

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.core.graphics.drawable.toBitmap
import com.example.beehive.data.app.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetInstalledAppsUseCase(
    private val packageManager: PackageManager,
) {
    private fun isSystemApp(appInfo: ApplicationInfo): Boolean {
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }

    suspend operator fun invoke(): List<AppInfo> = withContext(Dispatchers.IO) {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter {
                !isSystemApp(it)
            }.map {
                AppInfo(
                    name = it.loadLabel(packageManager).toString(),
                    packageName = it.packageName,
                    icon = it.loadIcon(packageManager).toBitmap()
                )
            }.sortedWith(
                compareBy {
                    it.name
                }
            )
    }
}

