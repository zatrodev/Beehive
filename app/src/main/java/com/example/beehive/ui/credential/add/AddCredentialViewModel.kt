package com.example.beehive.ui.credential.add

import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AddCredentialViewModel(
    private val userRepository: UserRepository,
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(AddPasswordUiState())
    private var installedApps: List<InstalledApp> = emptyList()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            installedApps = getInstalledAppsUseCase()
            userRepository.getAllUsers().collectLatest { users ->
                uiState = uiState.copy(
                    user = users.firstOrNull(),
                    users = users,
                    mutableInstalledApps = installedApps
                )
            }
        }
    }

    fun updateName(input: String) {
        uiState = uiState.copy(
            name = input,
            packageName = installedApps.find { it.name == input }?.packageName ?: input,
            icon = installedApps.find { it.name == input }?.icon,
            mutableInstalledApps = installedApps.filter { it.name.contains(input) }
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

    fun createCredential() {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.insertCredential(
                uiState.toCredential(
                    id = credentialRepository.getNextId() + 1,
                    userId = uiState.user!!.id
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
    val mutableInstalledApps: List<InstalledApp> = emptyList(),
)

fun AddPasswordUiState.toCredential(id: Int, userId: Int): Credential = Credential(
    id = id,
    username = username,
    password = password,
    userId = userId,
    app = PasswordApp(
        name = name,
        packageName = packageName
    )
)
