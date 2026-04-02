package com.onthecrow.nomadrates.currency.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class CurrencyRatesMetadataDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    fun observeLastRatesTimestamp(): Flow<Long?> {
        return dataStore.data.map { preferences ->
            preferences[LAST_RATES_TIMESTAMP_KEY]
        }
    }

    suspend fun getLastManualRefreshAt(): Long? {
        return dataStore.data.map { preferences ->
            preferences[LAST_MANUAL_REFRESH_AT_KEY]
        }.first()
    }

    fun observeLastManualRefreshAt(): Flow<Long?> {
        return dataStore.data.map { preferences ->
            preferences[LAST_MANUAL_REFRESH_AT_KEY]
        }
    }

    fun observeLatestVisibleRatesTimestamp(): Flow<Long?> {
        return combine(
            observeLastRatesTimestamp(),
            observeLastManualRefreshAt(),
        ) { backendTimestamp, manualRefreshTimestamp ->
            listOfNotNull(backendTimestamp, manualRefreshTimestamp).maxOrNull()
        }
    }

    suspend fun saveLastManualRefreshAt(timestampMillis: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_MANUAL_REFRESH_AT_KEY] = timestampMillis
        }
    }

    suspend fun saveLastRatesTimestampIfNewer(timestamp: Long) {
        val normalizedTimestamp = timestamp.normalizeEpochMillis()
        dataStore.edit { preferences ->
            val currentTimestamp = preferences[LAST_RATES_TIMESTAMP_KEY]
            if (currentTimestamp == null || normalizedTimestamp > currentTimestamp) {
                preferences[LAST_RATES_TIMESTAMP_KEY] = normalizedTimestamp
            }
        }
    }

    private companion object {
        val LAST_RATES_TIMESTAMP_KEY = longPreferencesKey("last_rates_timestamp")
        val LAST_MANUAL_REFRESH_AT_KEY = longPreferencesKey("last_manual_refresh_at")
    }
}

private fun Long.normalizeEpochMillis(): Long {
    return if (this < 10_000_000_000L) this * 1_000 else this
}
