package com.example.beehive.ui.credential.add

import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetInstalledAppsUseCase.InstalledApp
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

class AddCredentialViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : ViewModel(), PasswordInput {
    private val userId: Int = savedStateHandle.get<Int>("userId")!!
    override var uiState by mutableStateOf(AddPasswordUiState())
    override lateinit var installedApps: List<InstalledApp>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            installedApps = getInstalledAppsUseCase()
            uiState = uiState.copy(
                user = userRepository.getUserStream(userId).filterNotNull().first(),
                users = userRepository.getAllUsersStream().filterNotNull().first(),
                installedApps = installedApps
            )

        }
    }

    fun onCreateCredential() {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.insertCredential(
                uiState.toPassword(
                    id = credentialRepository.getNextId() + 1,
                    userId = uiState.user?.id ?: userId
                )
            )
        }
    }
}

data class AddPasswordUiState(
    val name: String = "",
    val username: String = "",
    val packageName: String = "",
    val icon: Drawable? = null,
    val password: String = generatePassword(1),
    val user: User? = null,
    val users: List<User> = emptyList(),
    var installedApps: List<InstalledApp> = emptyList(),
)

fun AddPasswordUiState.toPassword(id: Int, userId: Int): Credential = Credential(
    id = id,
    username = username,
    password = password,
    userId = userId,
    app = PasswordApp(
        name = name,
        packageName = packageName
    )
)
