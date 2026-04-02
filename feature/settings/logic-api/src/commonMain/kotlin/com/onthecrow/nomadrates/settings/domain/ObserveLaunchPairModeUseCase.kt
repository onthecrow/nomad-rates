package com.onthecrow.nomadrates.settings.domain

import kotlinx.coroutines.flow.Flow

interface ObserveLaunchPairModeUseCase {
    operator fun invoke(): Flow<LaunchPairMode>
}
