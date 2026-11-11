package com.onthecrow.nomadrates.currency

import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import com.onthecrow.nomadrates.currency.domain.SetCurrencyFavoriteUseCase
import kotlinx.coroutines.flow.first

internal class SetCurrencyFavoriteUseCaseImpl(
    private val currencyRepository: CurrencyRepository,
) : SetCurrencyFavoriteUseCase {
    override suspend fun invoke(currencyCode: String, isFavourite: Boolean) {
        val currency = currencyRepository.getCurrencyFlow(currencyCode).first() ?: return
        if (currency.isFavourite == isFavourite) return
        currencyRepository.saveCurrency(currency.copy(isFavourite = isFavourite))
    }
}
