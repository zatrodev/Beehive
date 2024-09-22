package com.example.beehive.domain

import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.data.users.User
import com.example.beehive.data.users.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetPasswordWithUserUseCase(
    private val passwordsRepository: PasswordsRepository,
    private val usersRepository: UsersRepository,
) {
    data class PasswordWithUser(
        val id: Int,
        val password: String,
        val username: String,
        val user: User,
    )

    operator fun invoke(id: Int): Flow<PasswordWithUser> =
        passwordsRepository.getPasswordStream(id).map { password ->
            val user = usersRepository.getUserStream(password.userId).filterNotNull().first()
            PasswordWithUser(password.id, password.password, password.username, user)
        }
}