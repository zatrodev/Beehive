package com.example.beehive.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.beehive.BeehiveApplication
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.domain.GetPasswordsOfUserByUriUseCase
import com.example.beehive.domain.GetPasswordsWithIconsOfUserUseCase
import com.example.beehive.ui.home.HomeViewModel
import com.example.beehive.ui.password.add.AddPasswordViewModel
import com.example.beehive.ui.password.edit.EditPasswordViewModel
import com.example.beehive.ui.password.view.ViewPasswordViewModel

object BeehiveViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AddPasswordViewModel(
                beehiveApplication().container.passwordsRepository,
                GetInstalledAppsUseCase(beehiveApplication().container.packageManager)
            )
        }

        initializer {
            EditPasswordViewModel(
                this.createSavedStateHandle(),
                beehiveApplication().container.passwordsRepository,
                GetInstalledAppsUseCase(beehiveApplication().container.packageManager)

            )
        }

        initializer {
            HomeViewModel(
                GetPasswordsWithIconsOfUserUseCase(
                    beehiveApplication().container.passwordsRepository,
                    GetInstalledAppsUseCase(beehiveApplication().container.packageManager)
                ),
                beehiveApplication().container.usersRepository,
            )
        }

        initializer {
            ViewPasswordViewModel(
                this.createSavedStateHandle(),
                beehiveApplication().container.passwordsRepository,
                beehiveApplication().container.usersRepository,
                GetPasswordsOfUserByUriUseCase(
                    beehiveApplication().container.passwordsRepository,
                )
            )
        }
    }
}

fun CreationExtras.beehiveApplication(): BeehiveApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BeehiveApplication)
