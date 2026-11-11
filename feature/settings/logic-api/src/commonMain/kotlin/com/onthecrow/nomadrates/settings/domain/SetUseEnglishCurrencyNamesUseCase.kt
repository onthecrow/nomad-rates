package com.onthecrow.nomadrates.settings.domain

interface SetUseEnglishCurrencyNamesUseCase {
    suspend operator fun invoke(isEnabled: Boolean)
}
