package com.onthecrow.nomadrates.settings.data

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

internal interface SettingsRepository {
    fun observeLaunchPairMode(): Flow<LaunchPairMode>

    suspend fun setLaunchPairMode(mode: LaunchPairMode)

    fun observeDefaultPair(): Flow<SelectedConversionPair>

    suspend fun setDefaultPair(pair: SelectedConversionPair)

    fun observeShowFeaturedPairs(): Flow<Boolean>

    suspend fun setShowFeaturedPairs(isEnabled: Boolean)

    fun observeShowFeaturedCurrencies(): Flow<Boolean>

    suspend fun setShowFeaturedCurrencies(isEnabled: Boolean)

    fun observeThemeMode(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
