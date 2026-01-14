package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import com.onthecrow.nomadrates.currency.domain.ToggleCurrencyFavoriteUseCase
import kotlinx.coroutines.flow.first

internal class ToggleCurrencyFavoriteUseCaseImpl(
    private val currencyRepository: CurrencyRepository
): ToggleCurrencyFavoriteUseCase {
    override suspend fun invoke(currencyCode: String) {
        val currency = currencyRepository.getCurrencyFlow(currencyCode).first() ?: return
        currencyRepository.saveCurrency(currency.copy(isFavourite = currency.isFavourite.not()))
    }
}
