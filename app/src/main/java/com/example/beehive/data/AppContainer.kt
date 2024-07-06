package com.example.beehive.data

import android.content.Context

interface AppContainer {
    val passwordsRepository: PasswordsRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    override val passwordsRepository: PasswordsRepository by lazy {
        PasswordsRepositoryImpl(BeehiveDatabase.getDatabase(applicationContext).passwordDao())
    }
}