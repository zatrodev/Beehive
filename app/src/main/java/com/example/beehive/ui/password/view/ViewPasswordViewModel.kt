package com.example.beehive.ui.password.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.data.users.UsersRepository
import com.example.beehive.domain.GetPasswordsOfUserByUriUseCase
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ViewPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val passwordsRepository: PasswordsRepository,
    private val usersRepository: UsersRepository,
    private val getPasswordsOfUserByUriUseCase: GetPasswordsOfUserByUriUseCase
) : ViewModel() {
    var uiState by mutableStateOf(ViewPasswordUiState())
    private val uri: String = savedStateHandle.get<String>("uri")!!
    private val userId: Int = savedStateHandle.get<Int>("userId")!!

    init {
        viewModelScope.launch {
            uiState = ViewPasswordUiState(
                // TODO: ask the user to add a default user for the application
                email = usersRepository.getUserStream(userId).first().email,
                passwords = getPasswordsOfUserByUriUseCase(uri, userId)
                    .filterNotNull()
                    .first()
            )
        }
    }

    fun deletePassword(id: Int) {
        viewModelScope.launch {
            passwordsRepository.deletePassword(id)
        }
    }
}

data class ViewPasswordUiState(
    val email: String = "",
    val passwords: List<Password> = emptyList()
)
