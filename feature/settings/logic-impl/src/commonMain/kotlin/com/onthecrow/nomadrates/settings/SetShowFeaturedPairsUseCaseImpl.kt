package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.SetShowFeaturedPairsUseCase

internal class SetShowFeaturedPairsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetShowFeaturedPairsUseCase {
    override suspend fun invoke(isEnabled: Boolean) {
        settingsRepository.setShowFeaturedPairs(isEnabled)
    }
}
