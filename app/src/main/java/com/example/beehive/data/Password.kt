package com.example.beehive.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey val id: Int,
    val site: String,
    val password: String = ""
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        return site.contains(query, ignoreCase = true)
    }
}