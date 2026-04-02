package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsUseCasesTest {
    private val repository = FakeSettingsRepository()

    @Test
    fun `observe use cases emit default values`() = runTest {
        assertEquals(
            LaunchPairMode.REMEMBER_LAST_PAIR,
            ObserveLaunchPairModeUseCaseImpl(repository).invoke().value(),
        )
        assertEquals(
            true,
            ObserveShowFeaturedPairsUseCaseImpl(repository).invoke().value(),
        )
        assertEquals(
            true,
            ObserveShowFeaturedCurrenciesUseCaseImpl(repository).invoke().value(),
        )
        assertEquals(
            ThemeMode.SYSTEM,
            ObserveThemeModeUseCaseImpl(repository).invoke().value(),
        )
        assertEquals(
            SelectedConversionPair.DEFAULT,
            ObserveDefaultPairUseCaseImpl(repository).invoke().value(),
        )
    }

    @Test
    fun `set use cases update repository state`() = runTest {
        SetLaunchPairModeUseCaseImpl(repository).invoke(LaunchPairMode.USE_DEFAULT_PAIR)
        SetDefaultPairUseCaseImpl(repository).invoke(SelectedConversionPair("GBP", "JPY"))
        SetShowFeaturedPairsUseCaseImpl(repository).invoke(false)
        SetShowFeaturedCurrenciesUseCaseImpl(repository).invoke(false)
        SetThemeModeUseCaseImpl(repository).invoke(ThemeMode.DARK)

        assertEquals(LaunchPairMode.USE_DEFAULT_PAIR, repository.launchPairMode.value)
        assertEquals(SelectedConversionPair("GBP", "JPY"), repository.defaultPair.value)
        assertEquals(false, repository.showFeaturedPairs.value)
        assertEquals(false, repository.showFeaturedCurrencies.value)
        assertEquals(ThemeMode.DARK, repository.themeMode.value)
    }
}

private suspend fun <T> Flow<T>.value(): T = first()

private class FakeSettingsRepository : SettingsRepository {
    val launchPairMode = MutableStateFlow(LaunchPairMode.REMEMBER_LAST_PAIR)
    val defaultPair = MutableStateFlow(SelectedConversionPair.DEFAULT)
    val showFeaturedPairs = MutableStateFlow(true)
    val showFeaturedCurrencies = MutableStateFlow(true)
    val themeMode = MutableStateFlow(ThemeMode.SYSTEM)

    override fun observeLaunchPairMode(): Flow<LaunchPairMode> = launchPairMode

    override suspend fun setLaunchPairMode(mode: LaunchPairMode) {
        launchPairMode.value = mode
    }

    override fun observeDefaultPair(): Flow<SelectedConversionPair> = defaultPair

    override suspend fun setDefaultPair(pair: SelectedConversionPair) {
        defaultPair.value = pair
    }

    override fun observeShowFeaturedPairs(): Flow<Boolean> = showFeaturedPairs

    override suspend fun setShowFeaturedPairs(isEnabled: Boolean) {
        showFeaturedPairs.value = isEnabled
    }

    override fun observeShowFeaturedCurrencies(): Flow<Boolean> = showFeaturedCurrencies

    override suspend fun setShowFeaturedCurrencies(isEnabled: Boolean) {
        showFeaturedCurrencies.value = isEnabled
    }

    override fun observeThemeMode(): Flow<ThemeMode> = themeMode

    override suspend fun setThemeMode(mode: ThemeMode) {
        themeMode.value = mode
    }
}
