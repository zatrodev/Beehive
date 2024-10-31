package com.example.beehive.domain

import androidx.compose.ui.graphics.asImageBitmap
import com.example.beehive.data.app.AppRepository
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.CredentialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCredentialsAndUserWithIconsSetUseCase(
    private val credentialRepository: CredentialRepository,
    private val appRepository: AppRepository,
) {
    operator fun invoke(): Flow<List<CredentialAndUser>> =
        credentialRepository.getAllCredentialsAndUser().map { credentials ->
            val packageNames = credentials.map { it.credential.app.packageName }
            val iconsMap =
                appRepository.getInstalledApps(packageNames).associateBy { it.packageName }

            credentials.map { credentialAndUser ->
                credentialAndUser.credential.app.icon =
                    iconsMap[credentialAndUser.credential.app.packageName]?.icon?.asImageBitmap()
                credentialAndUser
            }
        }
}