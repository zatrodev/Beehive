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
import com.example.beehive.ui.credential.PasswordInput
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AddCredentialViewModel(
    private val userRepository: UserRepository,
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : ViewModel(), PasswordInput {
    override var uiState by mutableStateOf(AddPasswordUiState())
    override lateinit var installedApps: List<InstalledApp>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            installedApps = getInstalledAppsUseCase()
            userRepository.getAllUsersStream().collectLatest { users ->
                uiState = uiState.copy(
                    users = users,
                    installedApps = installedApps
                )
            }
        }
    }

    fun onCreateCredential() {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.insertCredential(
                uiState.toPassword(
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
