package com.example.beehive.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsRepository(
    private val dataStore: DataStore<Preferences>,
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val RETENTION_PERIOD_DEFAULT = 30

        val RETENTION_PERIOD = intPreferencesKey("retention_period")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }


    init {
        coroutineScope.launch {
            dataStore.edit { settings ->
                if (!settings.contains(RETENTION_PERIOD)) {
                    settings[RETENTION_PERIOD] = RETENTION_PERIOD_DEFAULT
                }
                if (!settings.contains(FIRST_LAUNCH)) {
                    settings[FIRST_LAUNCH] = true
                }
            }
        }
    }

    val retentionPeriodFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[RETENTION_PERIOD] ?: RETENTION_PERIOD_DEFAULT
    }

    suspend fun updateRetentionPeriod(period: Int) {
        dataStore.edit { preferences ->
            preferences[RETENTION_PERIOD] = period
        }
    }

    suspend fun getFirstLaunch(): Boolean = dataStore.data.first()[FIRST_LAUNCH] ?: true


    suspend fun updateFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = isFirstLaunch
        }
    }

}