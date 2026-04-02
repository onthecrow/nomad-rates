package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.SetShowFeaturedCurrenciesUseCase

internal class SetShowFeaturedCurrenciesUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetShowFeaturedCurrenciesUseCase {
    override suspend fun invoke(isEnabled: Boolean) {
        settingsRepository.setShowFeaturedCurrencies(isEnabled)
    }
}
