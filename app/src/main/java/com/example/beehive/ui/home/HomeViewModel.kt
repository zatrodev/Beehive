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
import com.example.beehive.domain.GetCategorizedCredentialsByGroupingOption
import com.example.beehive.ui.DrawerItemsManager
import com.example.beehive.ui.DrawerItemsManager.DELETED_INDEX
import com.example.beehive.ui.settings.SettingsViewModel.Companion.RETENTION_PERIOD
import com.example.beehive.utils.addDaysToDate
import com.example.beehive.utils.filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val credentialRepository: CredentialRepository,
    private val userRepository: UserRepository,
    private val getCategorizedCredentialsByGroupingOption: GetCategorizedCredentialsByGroupingOption,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _homeUiState = MutableStateFlow<HomeScreenUiState>(HomeScreenUiState.Loading)
    private val _isRefreshing = MutableStateFlow(false)
    private val _groupingOption = MutableStateFlow<GroupingOption>(GroupingOption.ByUser)

    val uiState: StateFlow<HomeScreenUiState> = _homeUiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                _groupingOption.flatMapLatest { groupingOption ->
                    getCategorizedCredentialsByGroupingOption(groupingOption)
                },
                _query,
                _email,
                _isRefreshing,
                dataStore.data
            ) { credentialMap, query, email, isRefreshing, preferences ->

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
                    return@combine HomeScreenUiState.InputUser(
                        email = email,
                    )
                }

                HomeScreenUiState.Ready(
                    query = query,
                    credentialMap = credentialMap.filter(query),
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
                val temp = _groupingOption.value
                _groupingOption.value = if (_groupingOption.value == GroupingOption.ByApp)
                    GroupingOption.ByUser
                else
                    GroupingOption.ByApp

                _groupingOption.value = temp
            } catch (e: Exception) {
                _homeUiState.value = HomeScreenUiState.Error(e.message ?: "Unknown error")
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

    fun onGroupingOptionChange(groupingOption: GroupingOption) {
        _groupingOption.value = groupingOption
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

sealed class GroupingOption {
    data object ByApp : GroupingOption()
    data object ByUser : GroupingOption()

    fun getKey(credential: CredentialAndUser): Any = when (this) {
        ByApp -> credential.credential.app
        ByUser -> credential.user
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState

    data class Error(
        val errorMessage: String,
        val errorType: Throwable? = null,
    ) : HomeScreenUiState

    data object Tutorial : HomeScreenUiState

    data class InputUser(
        val email: String,
    ) : HomeScreenUiState

    data class Ready(
        val query: String,
        val credentialMap: Map<out Any, List<CredentialAndUser>>,
        val trashedCredentialsCount: Int? = null,
        val isRefreshing: Boolean = false,
    ) : HomeScreenUiState
}