package com.example.beehive.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beehive.data.users.User
import com.example.beehive.data.users.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddUserViewModel(
    private val usersRepository: UsersRepository,
) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onCreateUser(email: String) {
        viewModelScope.launch {
            usersRepository.insertUser(User(id = usersRepository.countUsers() + 1, email = email))
        }
    }
}