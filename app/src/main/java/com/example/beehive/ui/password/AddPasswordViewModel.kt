package com.example.beehive.ui.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.launch

class AddPasswordViewModel(
    private val passwordsRepository: PasswordsRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {
    private var installedApps by mutableStateOf(emptyList<InstalledApp>())
    var uiState by mutableStateOf(
        AddPasswordUiState()
    )

    init {
        viewModelScope.launch {
            installedApps = getInstalledAppsUseCase()
            uiState.installedApps = installedApps
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

    suspend fun createPassword(): Boolean {
        if (validateInput()) {
            passwordsRepository.insertPassword(uiState.toPassword(passwordsRepository.countPasswords() + 1))

            return true
        }

        return false
    }

    private fun validateInput(name: String = uiState.name): Boolean {
        return name.isNotBlank()
    }
}

data class AddPasswordUiState(
    val name: String = "",
    val packageName: String = "",
    val password: String = generatePassword(1),
    var installedApps: List<InstalledApp> = emptyList()
)

fun AddPasswordUiState.toPassword(id: Int): Password = Password(
    id = id,
    name = name,
    uri = packageName,
    password = password
)

fun Password.toPasswordUiState(): AddPasswordUiState = AddPasswordUiState(
    name = name,
    packageName = uri,
    password = password,
)