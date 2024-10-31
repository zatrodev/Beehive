package com.example.beehive.data.app

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "installed_apps")
data class AppInfo(
    @PrimaryKey
    val packageName: String,
    val name: String,
    val icon: Bitmap,
)