package com.example.beehive.domain

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetInstalledAppsUseCase(
    private val packageManager: PackageManager
) {
    companion object {
        var installedApplications: List<ApplicationInfo> = emptyList()
    }

    data class InstalledApp(
        val name: String,
        val packageName: String,
        val icon: Drawable
    )

    suspend operator fun invoke(): List<InstalledApp> = withContext(Dispatchers.IO) {
        if (installedApplications.isEmpty()) {
            installedApplications =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }

        return@withContext installedApplications
            .filter {
                (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0
            }
            .map {
                InstalledApp(
                    name = it.loadLabel(packageManager).toString(),
                    packageName = it.packageName,
                    icon = it.loadIcon(packageManager)
                )
            }.sortedWith(
                compareBy {
                    it.name
                }
            )

    }

}

