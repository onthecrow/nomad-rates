package com.onthecrow.nomadrates.settings.domain

import com.onthecrow.nomadrates.util.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ObserveThemeModeUseCase {
    operator fun invoke(): Flow<ThemeMode>
}
