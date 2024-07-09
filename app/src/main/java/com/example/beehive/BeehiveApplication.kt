package com.example.beehive

import android.app.Application
import com.example.beehive.data.BeehiveContainer
import com.example.beehive.data.BeehiveContainerImpl

class BeehiveApplication : Application() {
    lateinit var container: BeehiveContainer

    override fun onCreate() {
        super.onCreate()
        container = BeehiveContainerImpl(this)
    }
}