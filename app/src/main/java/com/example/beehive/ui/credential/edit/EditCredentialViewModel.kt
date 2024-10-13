package com.example.beehive.ui.credential.edit

import android.graphics.drawable.Drawable
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class EditCredentialViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val credentialRepository: CredentialRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
) : ViewModel() {
    private val passwordId: Int = savedStateHandle.get<Int>("id")!!
    private val userId = savedStateHandle.get<Int>("userId")!!
    private var installedApps: List<InstalledApp> = emptyList()

    private val _appName = MutableStateFlow("")
    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _user = MutableStateFlow(User(0, ""))
    private val _uiState = MutableStateFlow<EditPasswordUiState>(EditPasswordUiState.Loading)

    val uiState: StateFlow<EditPasswordUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            installedApps = getInstalledAppsUseCase()
            credentialRepository.getCredential(passwordId).first().let { credential ->
                _appName.value = credential.app.name
                _username.value = credential.username
                _password.value = credential.password
            }
            _user.value = userRepository.getUser(userId).first()

            combine(
                _appName,
                _username,
                _password,
                _user,
                userRepository.getAllUsers(),
            ) { appName, username, password, user, users ->
                EditPasswordUiState.Ready(
                    username = username,
                    password = password,
                    appName = appName,
                    user = user,
                    users = users,
                    packageName = installedApps.find { it.name == appName }?.packageName
                        ?: "",
                    icon = installedApps.find { it.name == appName }?.icon,
                    mutableInstalledApps = installedApps.filter { it.name.contains(appName) }
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

    fun updatePassword() {
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
        val mutableInstalledApps: List<InstalledApp>,
        val username: String = "",
        val icon: Drawable? = null,
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
//
//class EditCredentialViewModel(
//    savedStateHandle: SavedStateHandle,
//    private val userRepository: UserRepository,
//    private val credentialRepository: CredentialRepository,
//    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
//) : ViewModel(), PasswordInput {
//    private val passwordId: Int = savedStateHandle.get<Int>("id")!!
//    private val userId: Int = savedStateHandle.get<Int>("userId")!!
//
//    override var installedApps = emptyList<InstalledApp>()
//    override var uiState by mutableStateOf(AddPasswordUiState())
//
//    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            installedApps = getInstalledAppsUseCase()
//            uiState = credentialRepository.getCredentialStream(passwordId)
//                .filterNotNull()
//                .first()
//                .toPasswordUiState()
//                .copy(
//                    user = userRepository.getUserStream(userId).filterNotNull().first(),
//                    users = userRepository.getAllUsersStream().filterNotNull().first(),
//                    installedApps = installedApps
//                )
//        }
//    }
//
//    fun updatePassword() {
//        viewModelScope.launch(Dispatchers.IO) {
//            credentialRepository.updateCredential(
//                uiState.toPassword(
//                    passwordId,
//                    uiState.user?.id ?: userId
//                )
//            )
//        }
//    }
//}
//
//fun Credential.toPasswordUiState(): AddPasswordUiState = AddPasswordUiState(
//    username = username,
//    password = password,
//    name = app.name,
//    packageName = app.packageName,
//)