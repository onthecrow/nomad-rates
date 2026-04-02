package com.onthecrow.nomadrates.settings

import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.domain.ObserveThemeModeUseCase
import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

internal class ObserveThemeModeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveThemeModeUseCase {
    override fun invoke(): Flow<ThemeMode> = settingsRepository.observeThemeMode()
}
