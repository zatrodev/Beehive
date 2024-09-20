package com.example.beehive.data

import android.content.Context
import android.content.pm.PackageManager
import com.example.beehive.data.passwords.PasswordsRepository
import com.example.beehive.data.passwords.PasswordsRepositoryImpl
import com.example.beehive.data.users.UsersRepository
import com.example.beehive.data.users.UsersRepositoryImpl

interface BeehiveContainer {
    val passwordsRepository: PasswordsRepository
    val usersRepository: UsersRepository
    val packageManager: PackageManager
    val context: Context
}

class BeehiveContainerImpl(private val applicationContext: Context) : BeehiveContainer {
    override val passwordsRepository: PasswordsRepository by lazy {
        PasswordsRepositoryImpl(BeehiveDatabase.getDatabase(applicationContext).passwordDao())
    }

    override val usersRepository: UsersRepository by lazy {
        UsersRepositoryImpl(BeehiveDatabase.getDatabase(applicationContext).userDao())
    }

    override val packageManager: PackageManager
        get() = applicationContext.packageManager

    override val context: Context = applicationContext
}