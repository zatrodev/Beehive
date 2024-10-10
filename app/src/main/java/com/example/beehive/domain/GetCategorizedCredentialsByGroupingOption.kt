package com.example.beehive.domain

import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.ui.home.GroupingOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategorizedCredentialsByGroupingOption(
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) {
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