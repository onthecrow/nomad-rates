package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.ObserveShowFeaturedPairsUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveShowFeaturedPairsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveShowFeaturedPairsUseCase {
    override fun invoke(): Flow<Boolean> = settingsRepository.observeShowFeaturedPairs()
}
