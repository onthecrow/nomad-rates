package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.mapper.ConversionCurrencyStateMapper
import com.onthecrow.nomadrates.conversion.mapper.toConversionListItems
import com.onthecrow.nomadrates.entity.MoneyAmount
import com.onthecrow.nomadrates.entity.formatAdaptive
import com.onthecrow.nomadrates.ui.MutedClay
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.uicore.Reducer

internal class ConversionReducer : Reducer<ConversionState, ConversionEvent> {
    override suspend fun reduce(
        state: ConversionState,
        event: ConversionEvent
    ): ConversionState {
        return when (event) {
            is ConversionEvent.OnFromValueChange -> reduceFromValueChange(state, event)
            is ConversionEvent.OnToValueChange -> reduceToValueChange(state, event)
            is ConversionEvent.OnToValueConverted -> reduceToValueConverted(state, event)
            is ConversionEvent.OnFromValueConverted -> reduceFromValueConverted(state, event)
            is ConversionEvent.OnConversionPairsReceived -> reduceConversionPairsReceived(state, event)
            is ConversionEvent.OnActiveConversionPairChanged -> reduceActiveConversionPairChanged(state, event)
            else -> state
        }
    }

    private fun reduceFromValueConverted(
        state: ConversionState,
        event: ConversionEvent.OnFromValueConverted
    ): ConversionState {
        val conversionCurrencyState = state.conversionViewState.from
        val newConversionViewStat = state.conversionViewState.copy(
            from = conversionCurrencyState?.copy(
                conversionValue = MoneyAmount(
                    event.newValue
                ).formatAdaptive(conversionCurrencyState.currencyCode)
            )
        )
        return state.copy(conversionViewState = newConversionViewStat)
    }

    private fun reduceToValueConverted(
        state: ConversionState,
        event: ConversionEvent.OnToValueConverted
    ): ConversionState {
        val conversionCurrencyState = state.conversionViewState.to
        val newConversionViewStat = state.conversionViewState.copy(
            to = conversionCurrencyState?.copy(
                conversionValue = MoneyAmount(
                    event.newValue
                ).formatAdaptive(conversionCurrencyState.currencyCode)
            )
        )
        return state.copy(conversionViewState = newConversionViewStat)
    }

    private fun reduceConversionPairsReceived(
        state: ConversionState,
        event: ConversionEvent.OnConversionPairsReceived,
    ): ConversionState {
        return state.copy(conversionListItems = event.conversionPairs.toConversionListItems())
    }

    private fun reduceFromValueChange(
        state: ConversionState,
        event: ConversionEvent.OnFromValueChange,
    ): ConversionState {
        val newValue = event.value.filterAmountInput()
        val conversionViewState = state.conversionViewState
        return state.copy(
            conversionViewState = conversionViewState.copy(
                from = conversionViewState.from?.copy(conversionValue = newValue)
            ),
        )
    }

    private fun reduceToValueChange(
        state: ConversionState,
        event: ConversionEvent.OnToValueChange,
    ): ConversionState {
        val newValue = event.value.filterAmountInput()
        val conversionViewState = state.conversionViewState
        return state.copy(
            conversionViewState = conversionViewState.copy(
                to = conversionViewState.to?.copy(conversionValue = newValue)
            ),
        )
    }

    private fun reduceActiveConversionPairChanged(
        state: ConversionState,
        event: ConversionEvent.OnActiveConversionPairChanged,
    ): ConversionState {
        val conversionPair = event.conversionPair
        val historicalRates = conversionPair.historicalRates
        val conversionViewState = state.conversionViewState
        val chartColor = if (historicalRates.size >= 2 &&
            historicalRates.first() < historicalRates.last()
        ) {
            SageGreen
        } else {
            MutedClay
        }

        return state.copy(
            conversionViewState = conversionViewState.copy(
                from = ConversionCurrencyStateMapper.fromCurrency(
                    conversionPair.fromCurrency,
                    conversionViewState.from?.conversionValue ?: ""
                ),
                to = ConversionCurrencyStateMapper.fromCurrency(
                    conversionPair.toCurrency,
                    conversionViewState.to?.conversionValue ?: ""
                ),
                isFavourite = conversionPair.isFavourite,
            ),
            chartColor = chartColor,
            chartData = historicalRates,
        )
    }

    private fun String.filterAmountInput(): String {
        return buildString {
            var hasDot = false
            for (char in this@filterAmountInput) {
                if (char.isDigit()) {
                    append(char)
                } else if (!hasDot && (char == '.' || char == ',')) {
                    append('.')
                    hasDot = true
                }
            }
        }
    }
}
