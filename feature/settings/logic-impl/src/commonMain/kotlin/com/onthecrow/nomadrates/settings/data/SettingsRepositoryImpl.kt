package com.onthecrow.nomadrates.settings.data

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.data.datastore.SettingsPreferencesDataSource
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

internal class SettingsRepositoryImpl(
    private val settingsPreferencesDataSource: SettingsPreferencesDataSource,
) : SettingsRepository {
    override fun observeLaunchPairMode(): Flow<LaunchPairMode> {
        return settingsPreferencesDataSource.observeLaunchPairMode()
    }

    override suspend fun setLaunchPairMode(mode: LaunchPairMode) {
        settingsPreferencesDataSource.saveLaunchPairMode(mode)
    }

    override fun observeDefaultPair(): Flow<SelectedConversionPair> {
        return settingsPreferencesDataSource.observeDefaultPair()
    }

    override suspend fun setDefaultPair(pair: SelectedConversionPair) {
        settingsPreferencesDataSource.saveDefaultPair(pair)
    }

    override fun observeShowFeaturedPairs(): Flow<Boolean> {
        return settingsPreferencesDataSource.observeShowFeaturedPairs()
    }

    override suspend fun setShowFeaturedPairs(isEnabled: Boolean) {
        settingsPreferencesDataSource.saveShowFeaturedPairs(isEnabled)
    }

    override fun observeShowFeaturedCurrencies(): Flow<Boolean> {
        return settingsPreferencesDataSource.observeShowFeaturedCurrencies()
    }

    override suspend fun setShowFeaturedCurrencies(isEnabled: Boolean) {
        settingsPreferencesDataSource.saveShowFeaturedCurrencies(isEnabled)
    }

    override fun observeThemeMode(): Flow<ThemeMode> {
        return settingsPreferencesDataSource.observeThemeMode()
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        settingsPreferencesDataSource.saveThemeMode(mode)
    }
}
