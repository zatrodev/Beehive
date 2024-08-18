package com.example.beehive.ui.password.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.ui.password.add.AddPasswordUiState
import com.example.beehive.ui.password.add.toPassword
import com.example.beehive.ui.password.add.toPasswordUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val passwordsRepository: PasswordsRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {
    var uiState by mutableStateOf(AddPasswordUiState())
    private var installedApps by mutableStateOf(emptyList<InstalledApp>())
    private val passwordId: Int = savedStateHandle.get<Int>("id")!!

    init {
        viewModelScope.launch {
            installedApps = getInstalledAppsUseCase()
            uiState.installedApps = installedApps
            uiState = passwordsRepository.getPasswordStream(passwordId)
                .filterNotNull()
                .first()
                .toPasswordUiState()
        }
    }

    fun updateUiState(name: String, packageName: String, password: String) {
        uiState = uiState.copy(
            name = name,
            packageName = packageName,
            password = password
        )

        uiState.installedApps =
            if (uiState.name.isBlank()) {
                installedApps
            } else {
                installedApps.filter {
                    it.name.contains(uiState.name, ignoreCase = true)
                }
            }
    }

    suspend fun updatePassword(): Boolean {
        if (validateInput()) {
            passwordsRepository.updatePassword(uiState.toPassword(passwordId))

            return true
        }

        return false
    }

    private fun validateInput(name: String = uiState.name): Boolean {
        return name.isNotBlank()
    }
}