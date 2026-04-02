package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.ObserveDefaultPairUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveDefaultPairUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveDefaultPairUseCase {
    override fun invoke(): Flow<SelectedConversionPair> = settingsRepository.observeDefaultPair()
}
