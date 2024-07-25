package com.example.beehive.domain

import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.data.users.User
import com.example.beehive.data.users.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetPasswordsWithUserByUriUseCase(
    private val passwordsRepository: PasswordsRepository,
    private val usersRepository: UsersRepository
) {
    data class PasswordWithUser(
        val password: String,
        val user: User
    )

    operator fun invoke(uri: String): Flow<List<PasswordWithUser>> =
        passwordsRepository.getPasswordsByUri(uri).map { passwords ->
            passwords.map { password ->
                val user = usersRepository.getUser(password.userId).filterNotNull().first()
                PasswordWithUser(password.password, user)
            }
        }

}

