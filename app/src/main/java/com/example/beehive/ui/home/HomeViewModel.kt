package com.example.beehive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.Password
import com.example.beehive.data.PasswordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeViewModel(private val passwordsRepository: PasswordsRepository) : ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> =
        combine(
            _homeUiState.map { it.query },
            passwordsRepository.getAllPasswordsStream()
        ) { query, passwords ->
            val filteredPasswords = if (query.isBlank()) {
                passwords
            } else {
                passwords.filter {
                    it.site.contains(query, ignoreCase = true)
                }
            }
            HomeUiState(
                passwords = filteredPasswords,
                query = query
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    fun onQueryChange(query: String) {
        _homeUiState.update { currentState ->
            currentState.copy(
                query = query
            )
        }
    }

    suspend fun deletePassword(id: Int) {
        passwordsRepository.deletePassword(id)
    }
}

data class HomeUiState(val passwords: List<Password> = listOf(), var query: String = "")