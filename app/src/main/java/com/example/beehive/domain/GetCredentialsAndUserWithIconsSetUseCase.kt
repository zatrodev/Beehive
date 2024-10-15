package com.example.beehive.domain

import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.CredentialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCredentialsAndUserWithIconsSetUseCase(
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) {
    operator fun invoke(): Flow<List<CredentialAndUser>> =
        credentialRepository.getAllCredentialsAndUser().map { credentials ->
            credentials.map { credential ->
                credential.credential.app.icon =
                    getInstalledAppsUseCase().find { it.packageName == credential.credential.app.packageName }?.icon
            }
            credentials
        }
}