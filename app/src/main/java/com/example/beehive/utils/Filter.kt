package com.example.beehive.utils

import com.example.beehive.data.credential.CredentialAndUser

fun List<CredentialAndUser>.filter(query: String): List<CredentialAndUser> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isBlank()) return this

    return this.filter { credential ->
        listOf(
            credential.user.email,
            credential.credential.app.name
        ).any {
            it.contains(trimmedQuery, ignoreCase = true)
        }
    }
}