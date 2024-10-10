package com.example.beehive.utils

import com.example.beehive.data.Searchable
import com.example.beehive.data.credential.CredentialAndUser

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

fun Map<out Any, List<CredentialAndUser>>.filter(query: String): Map<out Any, List<CredentialAndUser>> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isBlank()) return this

    return this.mapValues { (key, value) ->
        value.filter { credential ->
            val keySearchText = if (key is Searchable) key.searchText else ""
            listOf(keySearchText, credential.user.email, credential.credential.username).any {
                it.contains(trimmedQuery, ignoreCase = true)
            }
        }
    }.filterValues { passwords ->
        passwords.isNotEmpty()
    }
}