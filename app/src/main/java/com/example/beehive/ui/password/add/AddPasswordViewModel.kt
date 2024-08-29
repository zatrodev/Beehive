package com.example.beehive.ui.password.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.launch

class AddPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val passwordsRepository: PasswordsRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {
    private var installedApps by mutableStateOf(emptyList<InstalledApp>())
    private val userId: Int = savedStateHandle.get<Int>("userId")!!
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

    fun onCreatePassword() {
        viewModelScope.launch {
            passwordsRepository.insertPassword(
                uiState.toPassword(
                    passwordsRepository.countPasswords() + 1,
                    userId
                )
            )
        }
    }
}

data class AddPasswordUiState(
    val name: String = "",
    val packageName: String = "",
    val password: String = generatePassword(1),
    var installedApps: List<InstalledApp> = emptyList()
)

fun AddPasswordUiState.toPassword(id: Int, userId: Int): Password = Password(
    id = id,
    name = name,
    uri = packageName,
    password = password,
    userId = userId
)

fun Password.toPasswordUiState(): AddPasswordUiState = AddPasswordUiState(
    name = name,
    packageName = uri,
    password = password,
)