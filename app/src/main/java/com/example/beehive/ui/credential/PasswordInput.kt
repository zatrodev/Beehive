package com.example.beehive.ui.credential

import com.example.beehive.data.user.User
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.ui.credential.add.AddPasswordUiState

interface PasswordInput {
    var installedApps: List<InstalledApp>
    var uiState: AddPasswordUiState

    fun updateName(input: String) {
        uiState = uiState.copy(
            name = input,
            packageName = uiState.installedApps.find { it.name == input }?.packageName ?: "",
            icon = uiState.installedApps.find { it.name == input }?.icon,
            installedApps = if (input.isBlank()) installedApps else installedApps.filter {
                it.name.contains(input, ignoreCase = true)
            }
        )
    }

    fun updateUsername(input: String) {
        uiState = uiState.copy(
            username = input
        )
    }

    fun updatePassword(input: String) {
        uiState = uiState.copy(
            password = input
        )
    }

    fun updateUser(input: User) {
        uiState = uiState.copy(
            user = input
        )
    }
}