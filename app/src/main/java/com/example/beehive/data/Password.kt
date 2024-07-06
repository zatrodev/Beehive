package com.example.beehive.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey val id: Int,
    val site: String,
    val url: String = "",
    val password: String = ""
) {
    fun doesMatchSearchQuery(query: String): Boolean {
        return site.contains(query, ignoreCase = true)
    }
}

val samplePasswords = listOf(
    Password(1, "Google", "https://www.google.com"),
    Password(2, "Facebook", "https://www.facebook.com"),
    Password(3, "Twitter", "https://www.twitter.com"),
    Password(4, "Amazon", "https://www.amazon.com"),
    Password(5, "GitHub", "https://www.github.com"),
    Password(6, "Netflix", "https://www.netflix.com"),
    Password(7, "Instagram", "https://www.instagram.com"),
    Password(8, "LinkedIn", "https://www.linkedin.com"),
    Password(9, "YouTube", "https://www.youtube.com"),
    Password(10, "Spotify", "https://www.spotify.com")
)