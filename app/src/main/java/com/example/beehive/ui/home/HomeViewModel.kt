package com.example.beehive.ui.home

import android.database.sqlite.SQLiteException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.credential.PasswordApp
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserRepository
import com.example.beehive.domain.GetCategorizedCredentialsAndUserByPackageUseCase
import com.example.beehive.utils.filter
import kotlinx.coroutines.Dispatchers
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
    private val credentialRepository: CredentialRepository,
    private val userRepository: UserRepository,
    private val getCategorizedCredentialsAndUserByPackageUseCase: GetCategorizedCredentialsAndUserByPackageUseCase,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    private var appCredentialMap =
        MutableStateFlow(getCategorizedCredentialsAndUserByPackageUseCase())
    private val _query = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _homeUiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<HomeScreenUiState> = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                appCredentialMap.flatMapLatest {
                    it
                },
                _query,
                _email,
                _isRefreshing,
                dataStore.data
            ) { appCredentialMap, query, email, isRefreshing, preferences ->
//                if (!preferences.contains(booleanPreferencesKey("tutorial_shown"))) {
//                    return@combine HomeScreenUiState.Tutorial
//                }


                if (userRepository.getNextId() == 0) {
                    return@combine HomeScreenUiState.InputUser(
                        email = email,
                    )
                }

                HomeScreenUiState.Ready(
                    query = query,
                    appCredentialMap = appCredentialMap.filter(query),
                    isRefreshing = isRefreshing
                )
            }.catch { throwable ->
                _homeUiState.value = when (throwable) {
                    is SQLiteException -> {
                        HomeScreenUiState.Error(
                            "Unauthenticated user detected!\nPlease restart the app and authenticate first.",
                            throwable
                        )
                    }

                    else -> HomeScreenUiState.Error(throwable.message)
                }
            }.collect {
                _homeUiState.value = it
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                appCredentialMap.value = getCategorizedCredentialsAndUserByPackageUseCase()
            } catch (e: Exception) {
                _homeUiState.value = HomeScreenUiState.Error(e.message)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onCreateUser(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insertUser(User(1, email))
        }
    }

    fun trashPassword(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.trashCredential(id)
        }
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState

    data class Error(
        val errorMessage: String? = null,
        val errorType: Throwable? = null,
    ) : HomeScreenUiState

    data object Tutorial : HomeScreenUiState

    data class InputUser(
        val email: String,
    ) : HomeScreenUiState

    data class Ready(
        val query: String,
        val appCredentialMap: Map<PasswordApp, List<CredentialAndUser>>,
        val isRefreshing: Boolean = false,
    ) : HomeScreenUiState
}