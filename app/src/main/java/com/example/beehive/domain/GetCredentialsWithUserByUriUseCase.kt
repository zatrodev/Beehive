package com.example.beehive.domain

import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GetCredentialsWithUserByUriUseCase(
    private val credentialRepository: CredentialRepository,
    private val userRepository: UserRepository,
) {
    data class PasswordWithUser(
        val id: Int,
        val password: String,
        val username: String,
        val user: User,
    )

    operator fun invoke(uri: String): Flow<List<PasswordWithUser>> =
        credentialRepository.getCredentialsByPackageNameStream(uri).map { passwords ->
            passwords.map { password ->
                val user = userRepository.getUserStream(password.userId).filterNotNull().first()
                PasswordWithUser(password.id, password.password, password.username, user)
            }
        }

}

