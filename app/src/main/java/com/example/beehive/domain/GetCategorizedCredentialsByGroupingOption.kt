package com.example.beehive.domain

import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.CredentialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategorizedCredentialsByGroupingOption(
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) {
    sealed class GroupingOption {
        data object ByApp : GroupingOption()
        data object ByUser : GroupingOption()

        fun getKey(credential: CredentialAndUser): Any = when (this) {
            ByApp -> credential.credential.app
            ByUser -> credential.user
        }
    }

    operator fun invoke(groupingOption: GroupingOption): Flow<Map<out Any, List<CredentialAndUser>>> {
        return credentialRepository.getAllCredentialsAndUser().map { credentials ->
            credentials.groupBy(groupingOption::getKey) { credential ->
                credential.credential.app.icon =
                    getInstalledAppsUseCase().find { it.packageName == credential.credential.app.packageName }?.icon
                credential
            }
        }
    }
}