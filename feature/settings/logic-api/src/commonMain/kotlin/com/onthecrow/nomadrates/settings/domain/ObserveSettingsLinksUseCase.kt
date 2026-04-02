package com.onthecrow.nomadrates.settings.domain

import kotlinx.coroutines.flow.Flow

fun interface ObserveSettingsLinksUseCase {
    operator fun invoke(): Flow<SettingsLinks>
}
