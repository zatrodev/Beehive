package com.example.beehive.domain

import android.graphics.drawable.Drawable
import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPasswordsWithIconsOfUserUseCase(
    private val passwordsRepository: PasswordsRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) {
    data class PasswordWithIcon(
        val self: Password,
        val icon: Drawable?
    )

    suspend operator fun invoke(userId: Int): Flow<List<PasswordWithIcon>> {
        val icons = getInstalledAppsUseCase()
        return passwordsRepository.getPasswordsByUserIdStream(userId).map { passwords ->
            passwords.map { password ->
                PasswordWithIcon(
                    password,
                    icons.find { it.packageName == password.uri }?.icon
                )
            }
        }
    }
}