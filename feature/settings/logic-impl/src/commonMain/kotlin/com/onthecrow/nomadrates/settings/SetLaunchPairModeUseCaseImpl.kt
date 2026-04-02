package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.settings.domain.SetLaunchPairModeUseCase

internal class SetLaunchPairModeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetLaunchPairModeUseCase {
    override suspend fun invoke(mode: LaunchPairMode) {
        settingsRepository.setLaunchPairMode(mode)
    }
}
