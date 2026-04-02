package com.onthecrow.nomadrates.settings.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SettingsPreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) {
    fun observeLaunchPairMode(): Flow<LaunchPairMode> {
        return dataStore.data.map { preferences ->
            preferences[LAUNCH_PAIR_MODE_KEY].toEnumOrDefault(LaunchPairMode.REMEMBER_LAST_PAIR)
        }
    }

    suspend fun saveLaunchPairMode(mode: LaunchPairMode) {
        dataStore.edit { preferences ->
            preferences[LAUNCH_PAIR_MODE_KEY] = mode.name
        }
    }

    fun observeDefaultPair(): Flow<SelectedConversionPair> {
        return dataStore.data.map { preferences ->
            SelectedConversionPair(
                fromCurrencyCode = preferences[DEFAULT_FROM_CURRENCY_CODE_KEY]
                    ?: SelectedConversionPair.DEFAULT.fromCurrencyCode,
                toCurrencyCode = preferences[DEFAULT_TO_CURRENCY_CODE_KEY]
                    ?: SelectedConversionPair.DEFAULT.toCurrencyCode,
            )
        }
    }

    suspend fun saveDefaultPair(pair: SelectedConversionPair) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_FROM_CURRENCY_CODE_KEY] = pair.fromCurrencyCode
            preferences[DEFAULT_TO_CURRENCY_CODE_KEY] = pair.toCurrencyCode
        }
    }

    fun observeShowFeaturedPairs(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[SHOW_FEATURED_PAIRS_KEY] ?: true
        }
    }

    suspend fun saveShowFeaturedPairs(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_FEATURED_PAIRS_KEY] = isEnabled
        }
    }

    fun observeShowFeaturedCurrencies(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[SHOW_FEATURED_CURRENCIES_KEY] ?: true
        }
    }

    suspend fun saveShowFeaturedCurrencies(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_FEATURED_CURRENCIES_KEY] = isEnabled
        }
    }

    fun observeThemeMode(): Flow<ThemeMode> {
        return dataStore.data.map { preferences ->
            preferences[THEME_MODE_KEY].toEnumOrDefault(ThemeMode.SYSTEM)
        }
    }

    suspend fun saveThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    private companion object {
        val LAUNCH_PAIR_MODE_KEY = stringPreferencesKey("launch_pair_mode")
        val DEFAULT_FROM_CURRENCY_CODE_KEY = stringPreferencesKey("default_from_currency_code")
        val DEFAULT_TO_CURRENCY_CODE_KEY = stringPreferencesKey("default_to_currency_code")
        val SHOW_FEATURED_PAIRS_KEY = booleanPreferencesKey("show_featured_pairs")
        val SHOW_FEATURED_CURRENCIES_KEY = booleanPreferencesKey("show_featured_currencies")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
}

private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T {
    return enumValues<T>().firstOrNull { value -> value.name == this } ?: default
}
