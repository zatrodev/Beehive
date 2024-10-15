package com.example.beehive

import android.app.Application
import android.view.autofill.AutofillManager
import com.example.beehive.data.container.BeehiveContainer
import com.example.beehive.data.container.BeehiveContainerImpl

class BeehiveApplication : Application() {
    lateinit var container: BeehiveContainer
    lateinit var autofillManager: AutofillManager

    override fun onCreate() {
        super.onCreate()
        container = BeehiveContainerImpl(this)
        autofillManager = getSystemService(AutofillManager::class.java)
    }
}