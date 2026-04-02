package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.SetThemeModeUseCase
import com.onthecrow.nomadrates.util.theme.ThemeMode

internal class SetThemeModeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetThemeModeUseCase {
    override suspend fun invoke(mode: ThemeMode) {
        settingsRepository.setThemeMode(mode)
    }
}
