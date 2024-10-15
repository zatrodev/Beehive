package com.example.beehive.auth.choose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.domain.GetCredentialsAndUserWithIconsSetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChooseCredentialViewModel(
    getCredentialsAndUserWithIconsSetUseCase: GetCredentialsAndUserWithIconsSetUseCase,
) : ViewModel() {
    private val _credentials = MutableStateFlow<List<CredentialAndUser>>(emptyList())
    val credentials = _credentials.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getCredentialsAndUserWithIconsSetUseCase().collect { credentials ->
                _credentials.value = credentials
            }
        }
    }
}