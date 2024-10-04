package com.example.beehive.ui.credential.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.user.UserRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.ui.credential.PasswordInput
import com.example.beehive.ui.credential.add.AddPasswordUiState
import com.example.beehive.ui.credential.add.toPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditCredentialViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : ViewModel(), PasswordInput {
    private val passwordId: Int = savedStateHandle.get<Int>("id")!!
    private val userId: Int = savedStateHandle.get<Int>("userId")!!

    override var installedApps = emptyList<InstalledApp>()
    override var uiState by mutableStateOf(AddPasswordUiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            installedApps = getInstalledAppsUseCase()
            uiState = credentialRepository.getCredentialStream(passwordId)
                .filterNotNull()
                .first()
                .toPasswordUiState()
                .copy(
                    user = userRepository.getUserStream(userId).filterNotNull().first(),
                    users = userRepository.getAllUsersStream().filterNotNull().first(),
                    installedApps = installedApps
                )
        }
    }

    fun updatePassword() {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.updateCredential(
                uiState.toPassword(
                    passwordId,
                    uiState.user?.id ?: userId
                )
            )
        }
    }
}

fun Credential.toPasswordUiState(): AddPasswordUiState = AddPasswordUiState(
    username = username,
    password = password,
    name = app.name,
    packageName = app.packageName,
)