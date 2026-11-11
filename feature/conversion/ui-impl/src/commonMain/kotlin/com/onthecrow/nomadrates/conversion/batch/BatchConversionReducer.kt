package com.onthecrow.nomadrates.conversion.batch

import com.onthecrow.nomadrates.conversion.batch.mapper.toBatchConversionListItem
import com.onthecrow.nomadrates.conversion.batch.mapper.withAmount
import com.onthecrow.nomadrates.conversion.mapper.ConversionCurrencyStateMapper
import com.onthecrow.nomadrates.conversion.mapper.filterAmountInput
import com.onthecrow.nomadrates.uicore.Reducer

internal class BatchConversionReducer : Reducer<BatchConversionState, BatchConversionEvent> {
    override suspend fun reduce(
        state: BatchConversionState,
        event: BatchConversionEvent,
    ): BatchConversionState = when (event) {
        is BatchConversionEvent.OnBatchConversionChanged -> reduceBatchConversionChanged(state, event)
        is BatchConversionEvent.OnAmountChange -> reduceAmountChange(state, event)
        BatchConversionEvent.OnBackPress,
        BatchConversionEvent.OnBaseCurrencyClick,
        BatchConversionEvent.OnAddCurrencyClick -> state
    }

    private fun reduceBatchConversionChanged(
        state: BatchConversionState,
        event: BatchConversionEvent.OnBatchConversionChanged,
    ): BatchConversionState.Content {
        val amount = (state as? BatchConversionState.Content)
            ?.baseCurrency
            ?.conversionValue
            ?: DEFAULT_AMOUNT
        val batchConversion = event.batchConversion

        return BatchConversionState.Content(
            baseCurrency = ConversionCurrencyStateMapper.fromCurrency(
                currency = batchConversion.baseCurrency,
                conversionValue = amount,
            ),
            favourites = batchConversion.targets.map { target ->
                target.toBatchConversionListItem(
                    amount = amount,
                    useEnglishCurrencyNames = event.useEnglishCurrencyNames,
                )
            },
        )
    }

    private fun reduceAmountChange(
        state: BatchConversionState,
        event: BatchConversionEvent.OnAmountChange,
    ): BatchConversionState {
        val contentState = state as? BatchConversionState.Content ?: return state
        val amount = event.value.filterAmountInput()
        if (amount == contentState.baseCurrency.conversionValue) return contentState

        return contentState.copy(
            baseCurrency = contentState.baseCurrency.copy(conversionValue = amount),
            favourites = contentState.favourites.map { item -> item.withAmount(amount) },
        )
    }

    internal companion object {
        const val DEFAULT_AMOUNT = "1"
    }
}
