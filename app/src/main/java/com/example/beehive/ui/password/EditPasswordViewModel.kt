package com.example.beehive.ui.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.PasswordsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val passwordsRepository: PasswordsRepository
) : ViewModel() {
    var passwordUiState by mutableStateOf(AddPasswordUiState())
    private val passwordId: Int = savedStateHandle.get<Int>("id")!!

    init {
        viewModelScope.launch {
            passwordUiState = passwordsRepository.getPasswordStream(passwordId)
                .filterNotNull()
                .first()
                .toPasswordUiState()
        }
    }

    fun updateUiState(name: String, password: String) {
        passwordUiState = passwordUiState.copy(
            name = name,
            password = password
        )
    }

    suspend fun updatePassword(): Boolean {
        if (validateInput()) {
            passwordsRepository.updatePassword(passwordUiState.toPassword(passwordId))

            return true
        }

        return false
    }

    private fun validateInput(name: String = passwordUiState.name): Boolean {
        return name.isNotBlank()
    }
}