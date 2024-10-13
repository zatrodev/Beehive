package com.example.beehive.ui.home

import android.database.sqlite.SQLiteException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserRepository
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.ui.DrawerItemsManager
import com.example.beehive.ui.DrawerItemsManager.DELETED_INDEX
import com.example.beehive.ui.settings.SettingsViewModel.Companion.RETENTION_PERIOD
import com.example.beehive.utils.addDaysToDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(
    private val credentialRepository: CredentialRepository,
    private val userRepository: UserRepository,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _homeUiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<HomeScreenUiState> = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                credentialRepository.getAllCredentialsAndUser(),
                _query,
                _isRefreshing,
                dataStore.data
            ) { credentials, query, isRefreshing, preferences ->

//                if (!preferences.contains(booleanPreferencesKey("tutorial_shown"))) {
//                    return@combine HomeScreenUiState.Tutorial
//                }

                if (DrawerItemsManager.allItems[DELETED_INDEX].badgeCount == null) {
                    DrawerItemsManager.setBadgeCount(
                        DELETED_INDEX,
                        credentialRepository.countTrashedCredentials().first()
                    )
                }

                if (userRepository.getNextId() == 0) {
                    return@combine HomeScreenUiState.InputUser
                }

                credentials.map { credential ->
                    credential.credential.app.icon =
                        getInstalledAppsUseCase().find { it.packageName == credential.credential.app.packageName }?.icon
                }

                HomeScreenUiState.Ready(
                    query = query,
                    credentials = credentials.filter(query),
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

                    else -> HomeScreenUiState.Error(throwable.message ?: "Unknown error")
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
                val credentials = credentialRepository.getAllCredentialsAndUser().first()
                _homeUiState.value = HomeScreenUiState.Ready(
                    query = _query.value,
                    credentials = credentials.filter(_query.value),
                    isRefreshing = false
                )
            } catch (e: Exception) {
                _homeUiState.value = HomeScreenUiState.Error(e.message ?: "Unknown error")
            } finally {
                _isRefreshing.value = false
            }
        }
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
            val retentionPeriod = dataStore.data.first()[intPreferencesKey(RETENTION_PERIOD)] ?: 30
            credentialRepository.trashCredential(
                id,
                addDaysToDate(
                    Date(),
                    retentionPeriod.toLong()
                )
            )
        }
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState

    data class Error(
        val errorMessage: String,
        val errorType: Throwable? = null,
    ) : HomeScreenUiState

    data object Tutorial : HomeScreenUiState

    data object InputUser : HomeScreenUiState

    data class Ready(
        val query: String,
        val credentials: List<CredentialAndUser>,
        val trashedCredentialsCount: Int? = null,
        val isRefreshing: Boolean = false,
    ) : HomeScreenUiState
}

fun List<CredentialAndUser>.filter(query: String): List<CredentialAndUser> {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isBlank()) return this

    return this.filter { credential ->
        listOf(
            credential.user.email,
            credential.credential.app.name
        ).any {
            it.contains(trimmedQuery, ignoreCase = true)
        }
    }
}