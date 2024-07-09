package com.example.beehive.data

import android.content.Context

interface BeehiveContainer {
    val passwordsRepository: PasswordsRepository
}

class BeehiveContainerImpl(private val applicationContext: Context) : BeehiveContainer {
    override val passwordsRepository: PasswordsRepository by lazy {
        PasswordsRepositoryImpl(BeehiveDatabase.getDatabase(applicationContext).passwordDao())
    }
}