package com.example.beehive.utils

import com.example.beehive.domain.GetPasswordsWithIconsOfUserUseCase.PasswordWithIcon

fun List<PasswordWithIcon>.filterByName(query: String): List<PasswordWithIcon> {
    if (query.isBlank()) {
        return this
    }

    return this.filter {
        it.self.name.contains(query, ignoreCase = true)
    }
}