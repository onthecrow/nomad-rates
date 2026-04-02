package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.ObserveShowFeaturedCurrenciesUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveShowFeaturedCurrenciesUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveShowFeaturedCurrenciesUseCase {
    override fun invoke(): Flow<Boolean> = settingsRepository.observeShowFeaturedCurrencies()
}
