package com.example.beehive.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.beehive.BeehiveApplication
import com.example.beehive.domain.GetCategorizedCredentialsWithUserByPackageUseCase
import com.example.beehive.domain.GetInstalledAppsUseCase
import com.example.beehive.ui.credential.add.AddCredentialViewModel
import com.example.beehive.ui.credential.edit.EditCredentialViewModel
import com.example.beehive.ui.home.HomeViewModel
import com.example.beehive.ui.user.AddUserViewModel

object BeehiveViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AddCredentialViewModel(
                this.createSavedStateHandle(),
                beehiveApplication().container.userRepository,
                beehiveApplication().container.credentialRepository,
                GetInstalledAppsUseCase(beehiveApplication().container.packageManager)
            )
        }

        initializer {
            EditCredentialViewModel(
                this.createSavedStateHandle(),
                beehiveApplication().container.userRepository,
                beehiveApplication().container.credentialRepository,
                GetInstalledAppsUseCase(beehiveApplication().container.packageManager)

            )
        }

        initializer {
            HomeViewModel(
                beehiveApplication().container.credentialRepository,
                beehiveApplication().container.userRepository,
                GetCategorizedCredentialsWithUserByPackageUseCase(
                    beehiveApplication().container.userRepository,
                    GetInstalledAppsUseCase(beehiveApplication().container.packageManager)
                )
            )
        }

        initializer {
            AddUserViewModel(
                beehiveApplication().container.userRepository
            )
        }
    }
}

fun CreationExtras.beehiveApplication(): BeehiveApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BeehiveApplication)
