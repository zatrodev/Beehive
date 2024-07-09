package com.example.beehive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.Password
import com.example.beehive.data.PasswordsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(passwordsRepository: PasswordsRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        passwordsRepository.getAllPasswordsStream().map { HomeUiState(passwords = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState()
            )
}

data class HomeUiState(val passwords: List<Password> = listOf())