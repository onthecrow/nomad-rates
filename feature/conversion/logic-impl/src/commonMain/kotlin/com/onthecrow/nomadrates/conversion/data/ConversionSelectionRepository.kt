package com.onthecrow.nomadrates.conversion.data

import com.onthecrow.nomadrates.conversion.data.datastore.ConversionPreferencesDataSource
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal class ConversionSelectionRepository(
    private val conversionPreferencesDataSource: ConversionPreferencesDataSource,
    private val sessionStore: ConversionSelectionSessionStore = ConversionSelectionSessionStore(),
) {
    // Cached persisted value to avoid repeatedly reading DataStore during launch/session flows.
    private var lastSavedSelectedConversionPair: SelectedConversionPair? = null

    fun observeSelectedConversionPairUpdates(): Flow<SelectedConversionPair> {
        return sessionStore.observeSelectedConversionPairUpdates()
    }

    suspend fun getSavedSelectedConversionPair(): SelectedConversionPair? {
        return lastSavedSelectedConversionPair
            ?: conversionPreferencesDataSource.getSelectedConversionPair()
                .first()
                .also { lastSavedSelectedConversionPair = it }
    }

    suspend fun getRuntimeSelectedConversionPair(
        defaultValue: SelectedConversionPair,
    ): SelectedConversionPair {
        return sessionStore.getRuntimeSelectedConversionPair(defaultValue)
    }

    suspend fun getCurrentSelectedConversionPairOrNull(): SelectedConversionPair? {
        return sessionStore.getRuntimeSelectedConversionPairOrNull() ?: getSavedSelectedConversionPair()
    }

    suspend fun saveSelectedConversionPair(selectedConversionPair: SelectedConversionPair) {
        lastSavedSelectedConversionPair = selectedConversionPair
        conversionPreferencesDataSource.saveSelectedConversionPair(selectedConversionPair)
        sessionStore.updateSelectedConversionPair(selectedConversionPair)
    }
}
