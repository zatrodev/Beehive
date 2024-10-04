package com.example.beehive.domain

import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.credential.PasswordApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategorizedCredentialsAndUserByPackageUseCase(
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) {
    operator fun invoke(): Flow<Map<PasswordApp, List<CredentialAndUser>>> =
        credentialRepository.getAllCredentialsAndUser().map { credentials ->
            credentials.groupBy { credential ->
                credential.credential.app.apply {
                    icon =
                        getInstalledAppsUseCase().find { it.packageName == credential.credential.app.packageName }?.icon
                }
            }
        }
}