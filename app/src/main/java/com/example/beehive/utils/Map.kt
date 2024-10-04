package com.example.beehive.utils

import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.PasswordApp

//fun Map<User, Map<PasswordApp, List<Credential>>>.filter(query: String): Map<User, Map<PasswordApp, List<Credential>>> {
//    val trimmedQuery = query.trim()
//    if (trimmedQuery.isBlank()) return this
//
//    return this.mapValues { user ->
//        user.value.mapValues { app ->
//            app.value.filter { credential ->
//                listOf(app.key.name, credential.username).any {
//                    it.contains(trimmedQuery, ignoreCase = true)
//                }
//            }
//        }.filterValues { passwords ->
//            passwords.isNotEmpty()
//        }
//    }
//}

fun Map<PasswordApp, List<CredentialAndUser>>.filter(query: String): Map<PasswordApp, List<CredentialAndUser>> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isBlank()) return this

    return this.mapValues { app ->
        app.value.filter { credential ->
            listOf(app.key.name, credential.user.email, credential.credential.username).any {
                it.contains(trimmedQuery, ignoreCase = true)
            }
        }
    }.filterValues { passwords ->
        passwords.isNotEmpty()
    }
}