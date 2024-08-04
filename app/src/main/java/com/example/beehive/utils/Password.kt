package com.example.beehive.utils

import com.example.beehive.data.passwords.Password

fun List<Password>.filterByName(query: String): List<Password> {
    if (query.isBlank()) {
        return this
    }

    return this.filter {
        it.name.contains(query, ignoreCase = true)
    }
}