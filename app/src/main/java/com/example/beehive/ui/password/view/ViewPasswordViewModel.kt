package com.example.beehive.ui.password.view

import android.graphics.drawable.Drawable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.data.users.UsersRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetPasswordsOfUserByUriUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ViewPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val passwordsRepository: PasswordsRepository,
    private val usersRepository: UsersRepository,
    private val getPasswordsOfUserByUriUseCase: GetPasswordsOfUserByUriUseCase,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {
    private var _viewPasswordUiState =
        MutableStateFlow<ViewPasswordUiState>(ViewPasswordUiState.Loading)
    private val packageName: String = savedStateHandle.get<String>("packageName")!!
    private val userId: Int = savedStateHandle.get<Int>("userId")!!
    private val refreshing = MutableStateFlow(false)

    var viewPasswordUiState: StateFlow<ViewPasswordUiState> = _viewPasswordUiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                refreshing,
                usersRepository.getUserStream(userId),
                getPasswordsOfUserByUriUseCase(packageName, userId),
            ) { refreshing, activeUser, passwords ->
                if (passwords.isEmpty())
                    return@combine ViewPasswordUiState.Back

                if (refreshing)
                    return@combine ViewPasswordUiState.Loading

                ViewPasswordUiState.Ready(
                    email = activeUser.email,
                    packageName = packageName,
                    name = passwords[0].name,
                    icon = getInstalledAppsUseCase().find { it.packageName == packageName }?.icon,
                    passwords = passwords
                )
            }.catch { throwable ->
                _viewPasswordUiState.value = ViewPasswordUiState.Error(throwable.message)
            }.collect {
                _viewPasswordUiState.value = it
            }
        }
    }

    fun getActiveUserId(): Int {
        return userId
    }

    fun deletePassword(id: Int) {
        viewModelScope.launch {
            passwordsRepository.deletePassword(id)
        }
    }
}

sealed interface ViewPasswordUiState {
    data object Back : ViewPasswordUiState
    data object Loading : ViewPasswordUiState

    data class Error(
        val errorMessage: String? = null
    ) : ViewPasswordUiState

    data class Ready(
        val email: String = "",
        val name: String = "",
        val packageName: String = "",
        val icon: Drawable? = null,
        val passwords: List<Password> = emptyList()
    ) : ViewPasswordUiState
}
