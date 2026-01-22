package com.onthecrow.nomadrates.conversion

import androidx.compose.ui.util.fastJoinToString
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
            is ConversionEvent.OnFromCurrencyChange -> reduceFromCurrencyChange(state, event)
            is ConversionEvent.OnToCurrencyChange -> reduceToCurrencyChange(state, event)
            is ConversionEvent.OnSwitchButtonPress -> reduceSwitchEvent(state)
            is ConversionEvent.OnToValueConverted -> state.copy(
                to = state.to?.copy(
                    conversionValue = MoneyAmount(
                        event.newValue
                    ).formatAdaptive(state.to.currencyCode)
                )
            )

            is ConversionEvent.OnFromValueConverted -> state.copy(
                from = state.from?.copy(
                    conversionValue = MoneyAmount(event.newValue).formatAdaptive(state.from.currencyCode)
                )
            )

            is ConversionEvent.OnHistoricalRatesChange -> reduceHistoricalRatesChange(state, event)
            is ConversionEvent.OnConversionPairsReceived -> reduceConversionPairsReceived(
                state,
                event
            )

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
        if (event.rates.size < 2) return state
        return state.copy(
            historicalRates = event.rates,
            historicalRatesColor = if (event.rates.first() > event.rates.last()) MutedClay else SageGreen
        )
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
        return state.copy(from = state.from?.copy(conversionValue = newValue))
    }

    private fun reduceToValueChange(
        state: ConversionState,
        event: ConversionEvent.OnToValueChange,
    ): ConversionState {
        val newValue = event.value.filterAmountInput()
        return state.copy(to = state.to?.copy(conversionValue = newValue))
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
