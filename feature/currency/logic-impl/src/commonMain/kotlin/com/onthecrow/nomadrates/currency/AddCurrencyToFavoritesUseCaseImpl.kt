package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import com.onthecrow.nomadrates.currency.domain.AddCurrencyToFavoritesUseCase
import kotlinx.coroutines.flow.first

internal class AddCurrencyToFavoritesUseCaseImpl(
    private val currencyRepository: CurrencyRepository,
) : AddCurrencyToFavoritesUseCase {
    override suspend fun invoke(currencyCode: String) {
        val currency = currencyRepository.getCurrencyFlow(currencyCode).first() ?: return
        if (currency.isFavourite) return
        currencyRepository.saveCurrency(currency.copy(isFavourite = true))
    }
}
