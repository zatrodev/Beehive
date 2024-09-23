package com.example.beehive.domain

import android.util.Log
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategorizedCredentialsWithUserByPackageUseCase(
    private val userRepository: UserRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) {
    operator fun invoke(): Flow<Map<User, Map<PasswordApp, List<Credential>>>> =
        userRepository.getUsersWithCredentials().map { userWithCredentials ->
            Log.d("GetCategorizedCredentialsOfUserByPackageUseCase", "invoke called")
            userWithCredentials.associate { userWithCredential ->
                userWithCredential.user to userWithCredential.credentials.groupBy { credential ->
                    credential.app.apply {
                        icon =
                            getInstalledAppsUseCase().find { it.packageName == credential.app.packageName }?.icon
                    }
                }
            }
        }
}