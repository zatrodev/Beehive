package com.example.beehive.data.passwords

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: edit userId

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey val id: Int,
    val name: String,
    val password: String = "",
    val uri: String = "",
    val userId: Int = 1,
)