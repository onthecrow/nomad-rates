package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.domain.ConversionDataState
import com.onthecrow.nomadrates.conversion.mapper.ConversionCurrencyStateMapper
import com.onthecrow.nomadrates.conversion.mapper.toConversionListItems
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ConversionViewState
import com.onthecrow.nomadrates.conversion.model.ListGroup
import com.onthecrow.nomadrates.entity.MoneyAmount
import com.onthecrow.nomadrates.entity.formatAdaptive
import com.onthecrow.nomadrates.ui.MutedClay
import com.onthecrow.nomadrates.ui.SageGreen
import com.onthecrow.nomadrates.uicore.Reducer
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.conversion_internet_required

internal class ConversionReducer : Reducer<ConversionState, ConversionEvent> {
    override suspend fun reduce(
        state: ConversionState,
        event: ConversionEvent
    ): ConversionState {
        return when (event) {
            is ConversionEvent.OnConversionDataChanged -> reduceConversionDataChanged(state, event)
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
            ConversionDataState.Loading -> ConversionState.Loading
            ConversionDataState.Error -> ConversionState.Error(Res.string.conversion_internet_required)
            is ConversionDataState.Content -> mapToContentState(state, dataState)
        }
    }

    private fun mapToContentState(
        state: ConversionState,
        dataState: ConversionDataState.Content,
    ): ConversionState.Content {
        val previousContent = state as? ConversionState.Content
        val activePair = dataState.activePair
        val historicalRates = activePair.historicalRates
        val chartColor = if (historicalRates.size >= 2 &&
            historicalRates.first() < historicalRates.last()
        ) {
            SageGreen
        } else {
            MutedClay
        }

        return ConversionState.Content(
            conversionViewState = ConversionViewState(
                from = ConversionCurrencyStateMapper.fromCurrency(
                    activePair.fromCurrency,
                    previousContent?.conversionViewState?.from?.conversionValue ?: "",
                ),
                to = ConversionCurrencyStateMapper.fromCurrency(
                    activePair.toCurrency,
                    previousContent?.conversionViewState?.to?.conversionValue ?: "",
                ),
                isFavourite = activePair.isFavourite,
            ),
            chartColor = chartColor,
            chartData = historicalRates,
            conversionListItems = dataState.conversionPairs.toConversionListItems(),
        )
    }

    private fun reduceHighlightConversionPair(
        state: ConversionState,
        event: ConversionEvent.HighlightConversionPair
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val itemToHighlight = contentState.conversionListItems.map { listItem ->
            val listItemData = listItem as? ConversionListItem.Data
            if (listItemData?.currencyPair == event.conversionPair &&
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
        event: ConversionEvent.OnFromValueConverted
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val conversionCurrencyState = contentState.conversionViewState.from ?: return state
        val newConversionViewStat = contentState.conversionViewState.copy(
            from = conversionCurrencyState.copy(
                conversionValue = MoneyAmount(
                    event.newValue
                ).formatAdaptive(conversionCurrencyState.currencyCode)
            )
        )
        return contentState.copy(conversionViewState = newConversionViewStat)
    }

    private fun reduceToValueConverted(
        state: ConversionState,
        event: ConversionEvent.OnToValueConverted
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val conversionCurrencyState = contentState.conversionViewState.to ?: return state
        val newConversionViewStat = contentState.conversionViewState.copy(
            to = conversionCurrencyState.copy(
                conversionValue = MoneyAmount(
                    event.newValue
                ).formatAdaptive(conversionCurrencyState.currencyCode)
            )
        )
        return contentState.copy(conversionViewState = newConversionViewStat)
    }

    private fun reduceFromValueChange(
        state: ConversionState,
        event: ConversionEvent.OnFromValueChange,
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val newValue = event.value.filterAmountInput()
        val conversionViewState = contentState.conversionViewState
        return contentState.copy(
            conversionViewState = conversionViewState.copy(
                from = conversionViewState.from?.copy(conversionValue = newValue)
            ),
        )
    }

    private fun reduceToValueChange(
        state: ConversionState,
        event: ConversionEvent.OnToValueChange,
    ): ConversionState {
        val contentState = state as? ConversionState.Content ?: return state
        val newValue = event.value.filterAmountInput()
        val conversionViewState = contentState.conversionViewState
        return contentState.copy(
            conversionViewState = conversionViewState.copy(
                to = conversionViewState.to?.copy(conversionValue = newValue)
            ),
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
