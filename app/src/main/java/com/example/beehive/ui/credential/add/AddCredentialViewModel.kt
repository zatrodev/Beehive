package com.example.beehive.ui.credential.add

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.app.AppInfo
import com.example.beehive.data.app.AppRepository
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserRepository
import com.example.beehive.utils.generatePassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AddCredentialViewModel(
    private val userRepository: UserRepository,
    private val credentialRepository: CredentialRepository,
    private val appRepository: AppRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddPasswordUiState())
    val uiState: StateFlow<AddPasswordUiState> = _uiState.asStateFlow()

    private val _appName = MutableStateFlow("")
    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _user = MutableStateFlow<User?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            com.example.beehive.utils.combine(
                userRepository.getAllUsers(),
                appRepository.getAllInstalledApps(),
                _appName,
                _username,
                _password,
                _user,
            ) { users, apps, appName, username, password, user ->
                AddPasswordUiState(
                    appName = appName,
                    username = username,
                    password = password,
                    user = user ?: users.firstOrNull(),
                    users = users,
                    packageName = apps.find { it.name == appName }?.packageName ?: "",
                    icon = apps.find { it.name == appName }?.icon?.asImageBitmap(),
                    installedApps = apps.filter {
                        it.name.contains(
                            appName,
                            ignoreCase = true
                        )
                    }
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun updateUsername(username: String) {
        _username.value = username
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateAppName(appName: String) {
        _appName.value = appName
    }

    fun updateUser(user: User) {
        _user.value = user
    }

    fun createCredential() {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.insertCredential(
                uiState.value.toCredential(
                    id = credentialRepository.getNextId(),
                    userId = uiState.value.user!!.id
                )
            )
        }
    }
}

data class AddPasswordUiState(
    val appName: String = "",
    val username: String = "",
    val packageName: String = "",
    val icon: ImageBitmap? = null,
    val password: String = generatePassword(1),
    val user: User? = null,
    val users: List<User> = emptyList(),
    val installedApps: List<AppInfo> = emptyList(),
)

fun AddPasswordUiState.toCredential(id: Int, userId: Int): Credential = Credential(
    id = id,
    username = username,
    password = password,
    userId = userId,
    app = PasswordApp(
        name = appName,
        packageName = packageName
    )
)
