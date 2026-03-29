package com.onthecrow.nomadrates.conversion.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ConversionPreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    fun getSelectedConversionPair(): Flow<SelectedConversionPair?> {
        return dataStore.data.map { preferences ->
            val fromCurrencyCode = preferences[FROM_CURRENCY_CODE_KEY]
            val toCurrencyCode = preferences[TO_CURRENCY_CODE_KEY]

            if (fromCurrencyCode == null || toCurrencyCode == null) {
                null
            } else {
                SelectedConversionPair(
                    fromCurrencyCode = fromCurrencyCode,
                    toCurrencyCode = toCurrencyCode,
                )
            }
        }
    }

    suspend fun saveSelectedConversionPair(selectedConversionPair: SelectedConversionPair) {
        dataStore.edit { preferences ->
            preferences[FROM_CURRENCY_CODE_KEY] = selectedConversionPair.fromCurrencyCode
            preferences[TO_CURRENCY_CODE_KEY] = selectedConversionPair.toCurrencyCode
        }
    }

    private companion object {
        val FROM_CURRENCY_CODE_KEY = stringPreferencesKey("from_currency_code")
        val TO_CURRENCY_CODE_KEY = stringPreferencesKey("to_currency_code")
    }
}
