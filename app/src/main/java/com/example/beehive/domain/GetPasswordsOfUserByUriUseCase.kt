package com.example.beehive.domain

import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPasswordsOfUserByUriUseCase(
    private val passwordsRepository: PasswordsRepository,
) {
    operator fun invoke(uri: String, userId: Int): Flow<List<Password>> =
        passwordsRepository.getPasswordsByUriStream(uri).map { passwords ->
            passwords.filter { password ->
                password.userId == userId
            }
        }
}

