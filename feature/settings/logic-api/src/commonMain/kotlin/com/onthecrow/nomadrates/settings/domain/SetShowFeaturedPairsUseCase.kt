package com.onthecrow.nomadrates.settings.domain

interface SetShowFeaturedPairsUseCase {
    suspend operator fun invoke(isEnabled: Boolean)
}
