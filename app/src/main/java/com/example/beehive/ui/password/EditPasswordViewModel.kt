package com.example.beehive.ui.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.PasswordsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditPasswordViewModel(
    savedStateHandle: SavedStateHandle,
    private val passwordsRepository: PasswordsRepository
) : ViewModel() {
    var passwordUiState by mutableStateOf(PasswordUiState())
    private val passwordId: Int = savedStateHandle.get<Int>("id")!!

    init {
        viewModelScope.launch {
            passwordUiState = passwordsRepository.getPasswordStream(passwordId)
                .filterNotNull()
                .first()
                .toPasswordUiState()
        }
    }

    fun updateUiState(site: String, password: String) {
        passwordUiState =
            PasswordUiState(site = site, password = password)
    }

    suspend fun updatePassword() {
        if (validateInput()) {
            passwordsRepository.updatePassword(passwordUiState.toPassword(passwordId))
        }
    }

    private fun validateInput(site: String = passwordUiState.site): Boolean {
        return site.isNotBlank()
    }
}