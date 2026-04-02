package com.onthecrow.nomadrates.settings.domain

import com.onthecrow.nomadrates.util.theme.ThemeMode

interface SetThemeModeUseCase {
    suspend operator fun invoke(mode: ThemeMode)
}
