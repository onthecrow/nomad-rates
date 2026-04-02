package com.onthecrow.nomadrates.settings.domain

interface SetShowFeaturedCurrenciesUseCase {
    suspend operator fun invoke(isEnabled: Boolean)
}
