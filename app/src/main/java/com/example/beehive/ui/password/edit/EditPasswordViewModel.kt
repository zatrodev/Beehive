package com.example.beehive.ui.password.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.data.users.UsersRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.ui.password.add.AddPasswordUiState
import com.example.beehive.ui.password.add.PasswordInput
import com.example.beehive.ui.password.add.toPassword
import com.example.beehive.ui.password.add.toPasswordUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val usersRepository: UsersRepository,
    private val passwordsRepository: PasswordsRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : ViewModel(), PasswordInput {
    private val passwordId: Int = savedStateHandle.get<Int>("id")!!
    private val userId: Int = savedStateHandle.get<Int>("userId")!!

    override var installedApps = emptyList<InstalledApp>()
    override var uiState by mutableStateOf(AddPasswordUiState())

    init {
        viewModelScope.launch {
            installedApps = getInstalledAppsUseCase()
            uiState = passwordsRepository.getPasswordStream(passwordId)
                .filterNotNull()
                .first()
                .toPasswordUiState()
                .copy(
                    user = usersRepository.getUserStream(userId).filterNotNull().first(),
                    users = usersRepository.getAllUsersStream().filterNotNull().first(),
                    installedApps = installedApps
                )
        }
    }

    fun updatePassword() {
        viewModelScope.launch {
            passwordsRepository.updatePassword(
                uiState.toPassword(
                    passwordId,
                    uiState.user?.id ?: userId
                )
            )
        }
    }
}