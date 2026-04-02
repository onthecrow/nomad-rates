package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.settings.domain.ObserveLaunchPairModeUseCase
import kotlinx.coroutines.flow.Flow

internal class ObserveLaunchPairModeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveLaunchPairModeUseCase {
    override fun invoke(): Flow<LaunchPairMode> = settingsRepository.observeLaunchPairMode()
}
