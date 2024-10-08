package com.example.beehive.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.utils.addDaysToDate
import com.example.beehive.utils.getDaysDifferenceFromNow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val credentialRepository: CredentialRepository,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    companion object {
        const val RETENTION_PERIOD = "retention_period"
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.collect { preferences ->
                _uiState.value =
                    SettingsUiState.Ready(preferences[intPreferencesKey(RETENTION_PERIOD)] ?: 30)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateRetentionPeriod(retentionPeriod: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[intPreferencesKey(RETENTION_PERIOD)] = retentionPeriod
            }

            val trashedCredentials = credentialRepository.getTrashedCredentials().first()
            trashedCredentials.forEach { credential ->
                val currentRetentionPeriod =
                    getDaysDifferenceFromNow(credential.credential.deletionDate!!)
                credentialRepository.updateDeletionDate(
                    credential.credential.id,
                    addDaysToDate(
                        credential.credential.deletionDate,
                        retentionPeriod.toLong() - currentRetentionPeriod
                    )
                )
            }
        }
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Ready(val retentionPeriod: Int) : SettingsUiState

}