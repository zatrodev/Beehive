package com.example.beehive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.users.User
import com.example.beehive.data.users.UsersRepository
import com.example.beehive.domain.GetPasswordsWithIconsOfUserUseCase
import com.example.beehive.domain.GetPasswordsWithIconsOfUserUseCase.PasswordWithIcon
import com.example.beehive.utils.filterByName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getPasswordsWithIconsOfUserUseCase: GetPasswordsWithIconsOfUserUseCase,
    private val usersRepository: UsersRepository,
) : ViewModel() {
    private val users = usersRepository.getAllUsersStream()
    private val _selectedUser = MutableStateFlow<User?>(null)
    private val _query = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _homeUiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    private val _refreshing = MutableStateFlow(false)

    val homeUiState: StateFlow<HomeScreenUiState> = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                users,
                _query,
                _email,
                _refreshing,
                _selectedUser.flatMapLatest { selectedUser ->
                    getPasswordsWithIconsOfUserUseCase(selectedUser?.id ?: 1)
                }
            ) { users, query, email, refreshing, userPasswords ->
                if (users.isEmpty()) {
                    return@combine HomeScreenUiState.InputUser(
                        email = email,
                    )
                }
                if (refreshing) {
                    return@combine HomeScreenUiState.Loading
                }

                HomeScreenUiState.Ready(
                    query = query,
                    users = users,
                    passwords = userPasswords.filterByName(query),
                    refreshing = refreshing
                )
            }.catch { throwable ->
                _homeUiState.value = HomeScreenUiState.Error(throwable.message)
            }.collect {
                _homeUiState.value = it
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshing.value = true
            _selectedUser.emit(User(getActiveUserId(), _email.value))
            _refreshing.value = false
        }
    }

    fun getActiveUserId(): Int {
        return _selectedUser.value?.id ?: 1
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onUserSelected(user: User) {
        _selectedUser.value = user
    }

    fun onCreateUser(email: String) {
        viewModelScope.launch {
            usersRepository.insertUser(User(1, email))
        }
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState

    data class Error(
        val errorMessage: String? = null,
    ) : HomeScreenUiState

    data class InputUser(
        val email: String,
    ) : HomeScreenUiState

    data class Ready(
        val query: String,
        val users: List<User> = emptyList(),
        val passwords: List<PasswordWithIcon> = emptyList(),
        val refreshing: Boolean = false,
    ) : HomeScreenUiState
}