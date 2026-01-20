package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.mapper.ConversionCurrencyStateMapper
import com.onthecrow.nomadrates.ui.MutedClay
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.uicore.Reducer

internal class ConversionReducer : Reducer<ConversionState, ConversionEvent> {
    override suspend fun reduce(
        state: ConversionState,
        event: ConversionEvent
    ): ConversionState {
        return when (event) {
            is ConversionEvent.OnFromValueChange -> state.copy(
                from = state.from?.copy(
                    conversionValue = event.value
                )
            )

            is ConversionEvent.OnToValueChange -> state.copy(to = state.to?.copy(conversionValue = event.value))
            is ConversionEvent.OnFromCurrencyChange -> reduceFromCurrencyChange(state, event)
            is ConversionEvent.OnToCurrencyChange -> reduceToCurrencyChange(state, event)
            is ConversionEvent.OnSwitchButtonPress -> reduceSwitchEvent(state)
            is ConversionEvent.OnToValueConverted -> state.copy(to = state.to?.copy(conversionValue = event.newValue))
            is ConversionEvent.OnFromValueConverted -> state.copy(from = state.from?.copy(conversionValue = event.newValue))
            is ConversionEvent.OnHistoricalRatesChange -> reduceHistoricalRatesChange(state, event)
            else -> state
        }
    }

    private fun reduceFromCurrencyChange(
        state: ConversionState,
        event: ConversionEvent.OnFromCurrencyChange,
    ): ConversionState {
        return state.copy(
            from = ConversionCurrencyStateMapper.fromCurrency(
                event.currency,
                state.from?.conversionValue ?: ""
            )
        )
    }

    private fun reduceHistoricalRatesChange(
        state: ConversionState,
        event: ConversionEvent.OnHistoricalRatesChange,
    ): ConversionState {
        return state.copy(historicalRates = event.rates, historicalRatesColor = if (event.rates.first() > event.rates.last()) MutedClay else SageGreen)
    }

    private fun reduceSwitchEvent(
        state: ConversionState,
    ): ConversionState {
        return state.copy(from = state.to, to = state.from)
    }

    private fun reduceToCurrencyChange(
        state: ConversionState,
        event: ConversionEvent.OnToCurrencyChange,
    ): ConversionState {
        return state.copy(
            to = ConversionCurrencyStateMapper.fromCurrency(
                event.currency,
                state.to?.conversionValue ?: ""
            )
        )
    }
}
