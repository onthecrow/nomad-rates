package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.SetDefaultPairUseCase

internal class SetDefaultPairUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetDefaultPairUseCase {
    override suspend fun invoke(pair: SelectedConversionPair) {
        settingsRepository.setDefaultPair(pair)
    }
}
