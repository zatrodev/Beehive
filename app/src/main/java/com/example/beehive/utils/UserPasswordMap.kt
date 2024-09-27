package com.example.beehive.utils

import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User

fun Map<User, Map<PasswordApp, List<Credential>>>.filter(query: String): Map<User, Map<PasswordApp, List<Credential>>> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isBlank()) return this

    return this.mapValues { user ->
        user.value.mapValues { app ->
            app.value.filter { credential ->
                listOf(app.key.name, credential.username).any {
                    it.contains(trimmedQuery, ignoreCase = true)
                }
            }
        }.filterValues { passwords ->
            passwords.isNotEmpty()
        }
    }
}