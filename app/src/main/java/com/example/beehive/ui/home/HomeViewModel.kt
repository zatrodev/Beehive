package com.example.beehive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.passwords.Password
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.data.users.User
import com.example.beehive.data.users.UsersRepository
import com.example.beehive.utils.filterByName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val passwordsRepository: PasswordsRepository,
    usersRepository: UsersRepository,
) : ViewModel() {
    private val users = usersRepository.getAllUsersStream()
    private val _selectedUser = MutableStateFlow<User?>(null)
    private val _query = MutableStateFlow("")
    private val _homeUiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    private val refreshing = MutableStateFlow(false)

    val homeUiState: StateFlow<HomeScreenUiState>
        get() = _homeUiState

    init {
        viewModelScope.launch {
            combine(
                users,
                _query,
                refreshing,
                _selectedUser.flatMapLatest { selectedUser ->
                    passwordsRepository.getPasswordsByUserIdStream(selectedUser?.id ?: 1)
                }
            ) { users, query, refreshing, featuredPasswords ->
                if (refreshing) {
                    return@combine HomeScreenUiState.Loading
                }

                HomeScreenUiState.Ready(
                    query = query,
                    users = users,
                    featuredPasswords = featuredPasswords.filterByName(query),
                )
            }.catch { throwable ->
                _homeUiState.value = HomeScreenUiState.Error(throwable.message)
            }.collect {
                _homeUiState.value = it
            }
        }
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onUserSelected(user: User) {
        _selectedUser.value = user
    }

    fun deletePassword(id: Int) {
        viewModelScope.launch {
            passwordsRepository.deletePassword(id)
        }
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState

    data class Error(
        val errorMessage: String? = null
    ) : HomeScreenUiState

    data class Ready(
        val query: String,
        val users: List<User> = emptyList(),
        val featuredPasswords: List<Password> = emptyList(),
    ) : HomeScreenUiState
}