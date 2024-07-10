package com.example.beehive.ui.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.beehive.data.Password
import com.example.beehive.data.PasswordsRepository

class AddPasswordViewModel(private val passwordsRepository: PasswordsRepository) : ViewModel() {
    var passwordUiState by mutableStateOf(PasswordUiState())

    fun updateUiState(site: String, password: String) {
        passwordUiState =
            PasswordUiState(site = site, password = password)
    }

    suspend fun createPassword() {
        if (validateInput()) {
            passwordsRepository.insertPassword(passwordUiState.toPassword(passwordsRepository.countPasswords() + 1))
        }
    }

    private fun validateInput(site: String = passwordUiState.site): Boolean {
        return site.isNotBlank()
    }
}

data class PasswordUiState(
    val site: String = "",
    val password: String = "",
)

fun PasswordUiState.toPassword(id: Int): Password = Password(
    id = id,
    site = site,
    password = password
)

fun Password.toPasswordUiState(): PasswordUiState = PasswordUiState(
    site = site,
    password = password
)