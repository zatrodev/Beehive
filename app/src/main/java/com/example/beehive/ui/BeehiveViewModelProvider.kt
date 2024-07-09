package com.example.beehive.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.beehive.BeehiveApplication
import com.example.beehive.ui.home.HomeViewModel
import com.example.beehive.ui.password.AddPasswordViewModel

object BeehiveViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AddPasswordViewModel(beehiveApplication().container.passwordsRepository)
        }

        initializer {
            HomeViewModel(beehiveApplication().container.passwordsRepository)
        }
    }
}

fun CreationExtras.beehiveApplication(): BeehiveApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BeehiveApplication)
