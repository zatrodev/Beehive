package com.example.beehive.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.settings.SettingsRepository
import com.example.beehive.utils.addDaysToDate
import com.example.beehive.utils.getDaysDifferenceFromNow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val credentialRepository: CredentialRepository,
    private val settings: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            settings.retentionPeriodFlow.collect { retentionPeriod ->
                _uiState.value = SettingsUiState.Ready(
                    retentionPeriod
                )
            }
        }
    }

    fun updateRetentionPeriod(retentionPeriod: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.updateRetentionPeriod(retentionPeriod)

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