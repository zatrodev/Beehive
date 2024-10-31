package com.example.beehive.ui.credential.edit

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.app.AppInfo
import com.example.beehive.data.app.AppRepository
import com.example.beehive.data.credential.Credential
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class EditCredentialViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val credentialRepository: CredentialRepository,
    private val appRepository: AppRepository,
) : ViewModel() {
    private val passwordId: Int = savedStateHandle.get<Int>("id")!!
    private val userId = savedStateHandle.get<Int>("userId")!!

    private val _appName = MutableStateFlow("")
    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _user = MutableStateFlow<User?>(null)
    private val _uiState = MutableStateFlow<EditPasswordUiState>(EditPasswordUiState.Loading)

    val uiState: StateFlow<EditPasswordUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.getCredential(passwordId).first().let { credential ->
                _appName.value = credential.app.name
                _username.value = credential.username
                _password.value = credential.password
            }
            _user.value = userRepository.getUser(userId).first()

            com.example.beehive.utils.combine(
                appRepository.getAllInstalledApps(),
                _appName,
                _username,
                _password,
                _user,
                userRepository.getAllUsers(),
            ) { installedApps, appName, username, password, user, users ->
                EditPasswordUiState.Ready(
                    username = username,
                    password = password,
                    appName = appName,
                    user = user ?: users.first(),
                    users = users,
                    packageName = installedApps.find { it.name == appName }?.packageName
                        ?: appName,
                    icon = installedApps.find { it.name == appName }?.icon?.asImageBitmap(),
                    installedApps = installedApps.filter {
                        it.name.contains(
                            appName,
                            ignoreCase = true
                        )
                    }
                )
            }.catch { throwable ->
                _uiState.value = EditPasswordUiState.Error(throwable.message ?: "Unknown error")
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

    fun updateCredential() {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.updateCredential(
                uiState.value.toCredential(passwordId)
            )
        }
    }
}

sealed interface EditPasswordUiState {
    data class Ready(
        val user: User,
        val users: List<User>,
        val appName: String,
        val packageName: String,
        val password: String,
        val installedApps: List<AppInfo>,
        val username: String = "",
        val icon: ImageBitmap? = null,
    ) : EditPasswordUiState

    data class Error(val errorMessage: String) : EditPasswordUiState
    data object Loading : EditPasswordUiState
}

fun EditPasswordUiState.toCredential(
    passwordId: Int,
): Credential = Credential(
    id = passwordId,
    username = (this as EditPasswordUiState.Ready).username,
    password = this.password,
    app = PasswordApp(
        name = this.appName,
        packageName = this.packageName,
    ),
    userId = this.user.id
)