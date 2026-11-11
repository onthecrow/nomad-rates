package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.SetUseEnglishCurrencyNamesUseCase

internal class SetUseEnglishCurrencyNamesUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetUseEnglishCurrencyNamesUseCase {
    override suspend fun invoke(isEnabled: Boolean) {
        settingsRepository.setUseEnglishCurrencyNames(isEnabled)
    }
}
