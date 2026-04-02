package com.onthecrow.nomadrates.settings.domain

interface SetLaunchPairModeUseCase {
    suspend operator fun invoke(mode: LaunchPairMode)
}
