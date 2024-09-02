package com.example.beehive.ui.password.add

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.data.users.User
import com.example.beehive.data.users.UsersRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface PasswordInput {
    var uiState: AddPasswordUiState
    fun updateName(input: String) {
        uiState = uiState.copy(
            name = input,
            packageName = uiState.installedApps.find { it.name == input }?.packageName ?: "",
            installedApps = if (input.isBlank()) uiState.installedApps else uiState.installedApps.filter {
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
}

class AddPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val usersRepository: UsersRepository,
    private val passwordsRepository: PasswordsRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel(), PasswordInput {
    private val userId: Int = savedStateHandle.get<Int>("userId")!!
    override var uiState by mutableStateOf(AddPasswordUiState())

    init {
        viewModelScope.launch {
            Log.d("USER ID", userId.toString())
            uiState = uiState.copy(
                user = usersRepository.getUserStream(userId).filterNotNull().first(),
                installedApps = getInstalledAppsUseCase()
            )

        }
    }

    fun onCreatePassword() {
        viewModelScope.launch {
            passwordsRepository.insertPassword(
                uiState.toPassword(
                    id = passwordsRepository.countPasswords() + 1,
                    userId = userId
                )
            )
        }
    }
}

data class AddPasswordUiState(
    val name: String = "",
    val username: String = "",
    val packageName: String = "",
    val password: String = generatePassword(1),
    val user: User? = null,
    var installedApps: List<InstalledApp> = emptyList()
)

fun AddPasswordUiState.toPassword(id: Int, userId: Int): Password = Password(
    id = id,
    name = name,
    username = username,
    uri = packageName,
    password = password,
    userId = userId
)

fun Password.toPasswordUiState(): AddPasswordUiState = AddPasswordUiState(
    name = name,
    username = username,
    packageName = uri,
    password = password,
)