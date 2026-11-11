package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.favouritesconversion.domain.ObserveFavouritesConversionUseCase
import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversion
import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversionTarget
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class ObserveFavouritesConversionUseCaseImpl(
    private val getSelectedConversionPairUseCase: GetSelectedConversionPairUseCase,
    private val getCurrencyListUseCase: GetCurrencyListUseCase,
) : ObserveFavouritesConversionUseCase {

    override fun invoke(): Flow<FavouritesConversion> {
        // The favourites conversion shares its base currency with the "from" side of the main screen.
        val baseCurrencyCodeFlow = getSelectedConversionPairUseCase()
            .map { selectedConversionPair -> selectedConversionPair.fromCurrencyCode }
            .distinctUntilChanged()

        return combine(
            baseCurrencyCodeFlow,
            getCurrencyListUseCase().filterNotNull(),
        ) { baseCurrencyCode, currencies ->
            val baseCurrency = currencies.firstOrNull { currency ->
                currency.code.equals(baseCurrencyCode, ignoreCase = true)
            } ?: return@combine null

            FavouritesConversion(
                baseCurrency = baseCurrency,
                targets = currencies
                    .filter { currency -> currency.isFavourite }
                    .sortedBy { currency -> currency.code.uppercase() }
                    .map { currency ->
                        FavouritesConversionTarget(
                            currency = currency,
                            conversionRate = currency.conversionRate / baseCurrency.conversionRate,
                        )
                    },
            )
        }
            .filterNotNull()
            .distinctUntilChanged()
    }
}
