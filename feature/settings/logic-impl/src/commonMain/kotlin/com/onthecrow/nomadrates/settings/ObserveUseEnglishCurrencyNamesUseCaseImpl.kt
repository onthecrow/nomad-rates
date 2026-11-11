package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.ObserveUseEnglishCurrencyNamesUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveUseEnglishCurrencyNamesUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveUseEnglishCurrencyNamesUseCase {
    override fun invoke(): Flow<Boolean> = settingsRepository.observeUseEnglishCurrencyNames()
}
