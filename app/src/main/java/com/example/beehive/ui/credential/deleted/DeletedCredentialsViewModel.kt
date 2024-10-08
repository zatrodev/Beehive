package com.example.beehive.ui.credential.deleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.data.credential.CredentialRepository
import com.example.beehive.ui.DrawerItemsManager
import com.example.beehive.ui.DrawerItemsManager.DELETED_INDEX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeletedCredentialsViewModel(
    private val credentialRepository: CredentialRepository,
) : ViewModel() {
    private val deletedCredentials = credentialRepository.getTrashedCredentials()
    private val _uiState =
        MutableStateFlow<DeletedCredentialsUiState>(DeletedCredentialsUiState.Loading)

    val uiState: StateFlow<DeletedCredentialsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.deleteExpiredCredentials()
            deletedCredentials.collect {
                DrawerItemsManager.setBadgeCount(
                    DELETED_INDEX,
                    it.size
                )
                _uiState.value = DeletedCredentialsUiState.Ready(it)
            }
        }
    }

    fun deleteCredential(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.deleteCredential(id)
        }
    }

    fun deleteAllCredentials() {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.deleteAllTrashedCredentials()
        }
    }

    fun restoreCredential(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            credentialRepository.restoreCredential(id)
        }
    }
}

sealed interface DeletedCredentialsUiState {
    data object Loading : DeletedCredentialsUiState
    data class Error(val errorMessage: String) :
        DeletedCredentialsUiState

    data class Ready(val deletedCredentials: List<CredentialAndUser>) : DeletedCredentialsUiState
}