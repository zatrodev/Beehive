package com.example.beehive.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.user.User
import com.example.beehive.data.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddUserViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onCreateUser(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.insertUser(User(id = userRepository.getNextId() + 1, email = email))
        }
    }
}