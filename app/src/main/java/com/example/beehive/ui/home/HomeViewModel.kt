package com.example.beehive.ui.home

import androidx.lifecycle.ViewModel
import com.example.beehive.data.Password
import com.example.beehive.data.samplePasswords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _passwords = MutableStateFlow(samplePasswords)
    val passwords: StateFlow<List<Password>> = _passwords.asStateFlow()

    fun onQueryChange(query: String) {
        _query.value = query
    }
}