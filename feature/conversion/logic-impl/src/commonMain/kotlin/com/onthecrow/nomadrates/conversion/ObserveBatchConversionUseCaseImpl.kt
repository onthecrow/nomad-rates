package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ObserveBatchConversionUseCase
import com.onthecrow.nomadrates.conversion.domain.model.BatchConversion
import com.onthecrow.nomadrates.conversion.domain.model.BatchConversionTarget
import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class ObserveBatchConversionUseCaseImpl(
    private val getSelectedConversionPairUseCase: GetSelectedConversionPairUseCase,
    private val getCurrencyListUseCase: GetCurrencyListUseCase,
) : ObserveBatchConversionUseCase {

    override fun invoke(): Flow<BatchConversion> {
        // The batch conversion shares its base currency with the "from" side of the main screen.
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

            BatchConversion(
                baseCurrency = baseCurrency,
                targets = currencies
                    .filter { currency -> currency.isFavourite }
                    .sortedBy { currency -> currency.code.uppercase() }
                    .map { currency ->
                        BatchConversionTarget(
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
