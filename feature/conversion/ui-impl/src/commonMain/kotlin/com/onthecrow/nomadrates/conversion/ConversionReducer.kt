package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.ConversionDataState
import com.onthecrow.nomadrates.conversion.mapper.ConversionCurrencyStateMapper
import com.onthecrow.nomadrates.conversion.mapper.toConversionListItems
import com.onthecrow.nomadrates.conversion.model.toChartTrend
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.conversion.model.ListGroup
import com.onthecrow.nomadrates.entity.MoneyAmount
import com.onthecrow.nomadrates.entity.formatAdaptive
import com.onthecrow.nomadrates.uicore.Reducer
import com.onthecrow.nomadrates.util.normalizeFormattedAmount
import com.onthecrow.nomadrates.util.toAmountFieldValue

internal class ConversionReducer : Reducer<ConversionState, ConversionEvent> {
    override suspend fun reduce(
        state: ConversionState,
        event: ConversionEvent,
    ): ConversionState {
        return when (event) {
            is ConversionEvent.OnConversionDataChanged -> reduceConversionDataChanged(state, event)
            is ConversionEvent.OnRetryStateChanged -> reduceRetryStateChanged(state, event)
            is ConversionEvent.OnFromValueChange -> reduceFromValueChange(state, event)
            is ConversionEvent.OnToValueChange -> reduceToValueChange(state, event)
            is ConversionEvent.OnToValueConverted -> reduceToValueConverted(state, event)
            is ConversionEvent.OnFromValueConverted -> reduceFromValueConverted(state, event)
            is ConversionEvent.HighlightConversionPair -> reduceHighlightConversionPair(state, event)
            else -> state
        }
    }

    private fun reduceConversionDataChanged(
        state: ConversionState,
        event: ConversionEvent.OnConversionDataChanged,
    ): ConversionState {
        return when (val dataState = event.dataState) {
            ConversionDataState.Loading -> {
                if (state is ConversionState.Error && state.isRetrying) state else ConversionState.Loading
            }
            ConversionDataState.Error -> ConversionState.Error()
            is ConversionDataState.Content -> mapToContentState(state, dataState)
        }
    }

    private fun reduceRetryStateChanged(
        state: ConversionState,
        event: ConversionEvent.OnRetryStateChanged,
    ): ConversionState {
        val errorState = state as? ConversionState.Error ?: return state
        return errorState.copy(isRetrying = event.isRetrying)
    }

    private fun mapToContentState(
        state: ConversionState,
        dataState: ConversionDataState.Content,
    ): ConversionState.Content {
        val previousContent = state as? ConversionState.Content
        val previousViewState = previousContent?.conversionViewState
        val activePair = dataState.activePair
        val historicalRates = activePair.historicalRates

        return ConversionState.Content(
            conversionViewState = ConversionViewState.Content(
                from = ConversionCurrencyStateMapper.fromCurrency(
                    activePair.fromCurrency,
                    previousViewState?.from?.conversionValue ?: INITIAL_FROM_AMOUNT,
                ),
                to = ConversionCurrencyStateMapper.fromCurrency(
                    activePair.toCurrency,
                    previousViewState?.to?.conversionValue
                        ?: MoneyAmount(activePair.conversionRate).toConvertedFieldValue(activePair.toCurrency.code),
                ),
                isFavourite = activePair.isFavourite,
            ),
            chartTrend = historicalRates.toChartTrend(),
            chartData = historicalRates,
            conversionListItems = dataState.conversionPairs.toConversionListItems(),
        )
    }

    private fun reduceHighlightConversionPair(
        state: ConversionState,
        event: ConversionEvent.HighlightConversionPair,
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val itemToHighlight = contentState.conversionListItems.map { listItem ->
            val listItemData = listItem as? ConversionListItem.Data
            if (
                listItemData?.currencyPair == event.conversionPair &&
                listItem.listGroup == ListGroup.FAVOURITE
            ) {
                listItemData.copy(highlighted = event.shouldHighlight)
            } else {
                listItem
            }
        }
        return contentState.copy(
            conversionListItems = itemToHighlight,
        )
    }

    private fun reduceFromValueConverted(
        state: ConversionState,
        event: ConversionEvent.OnFromValueConverted,
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val conversionViewState = contentState.conversionViewState ?: return state
        val conversionCurrencyState = conversionViewState.from
        val newConversionViewState = conversionViewState.copy(
            from = conversionCurrencyState.copy(
                conversionValue = MoneyAmount(event.newValue)
                    .toConvertedFieldValue(conversionCurrencyState.currencyCode)
            )
        )
        return contentState.copy(conversionViewState = newConversionViewState)
    }

    private fun reduceToValueConverted(
        state: ConversionState,
        event: ConversionEvent.OnToValueConverted,
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val conversionViewState = contentState.conversionViewState ?: return state
        val conversionCurrencyState = conversionViewState.to
        val newConversionViewState = conversionViewState.copy(
            to = conversionCurrencyState.copy(
                conversionValue = MoneyAmount(event.newValue)
                    .toConvertedFieldValue(conversionCurrencyState.currencyCode)
            )
        )
        return contentState.copy(conversionViewState = newConversionViewState)
    }

    private fun reduceFromValueChange(
        state: ConversionState,
        event: ConversionEvent.OnFromValueChange,
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val conversionViewState = contentState.conversionViewState ?: return state
        val newValue = event.value.toAmountFieldValue(previous = conversionViewState.from.conversionValue)
        return contentState.copy(
            conversionViewState = conversionViewState.copy(
                from = conversionViewState.from.copy(conversionValue = newValue)
            ),
        )
    }

    private fun reduceToValueChange(
        state: ConversionState,
        event: ConversionEvent.OnToValueChange,
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val conversionViewState = contentState.conversionViewState ?: return state
        val newValue = event.value.toAmountFieldValue(previous = conversionViewState.to.conversionValue)
        return contentState.copy(
            conversionViewState = conversionViewState.copy(
                to = conversionViewState.to.copy(conversionValue = newValue)
            ),
        )
    }

    /**
     * A converted amount as the amount field holds it: raw input like "1234.50", rounded the same
     * way as amounts are shown elsewhere.
     */
    private fun MoneyAmount.toConvertedFieldValue(currencyCode: String): String =
        formatAdaptive(currencyCode).normalizeFormattedAmount()

    internal companion object {
        /** The amount in the "from" field before the user typed anything. */
        const val INITIAL_FROM_AMOUNT = "1"
    }
}
