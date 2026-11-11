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
        is BatchConversionEvent.OnOpened -> reduceOpened(state, event)
        is BatchConversionEvent.OnAmountChange -> reduceAmountChange(state, event.value.filterAmountInput())
        BatchConversionEvent.OnBackPress,
        BatchConversionEvent.OnBaseCurrencyClick,
        BatchConversionEvent.OnAddCurrencyClick -> state
    }

    private fun reduceBatchConversionChanged(
        state: BatchConversionState,
        event: BatchConversionEvent.OnBatchConversionChanged,
    ): BatchConversionState.Content {
        val amount = state.amount
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

    private fun reduceOpened(
        state: BatchConversionState,
        event: BatchConversionEvent.OnOpened,
    ): BatchConversionState {
        val loadingState = state as? BatchConversionState.Loading ?: return state

        return loadingState.copy(
            amount = event.amount.filterAmountInput().ifEmpty { loadingState.amount },
            baseCurrencyCode = event.baseCurrencyCode.ifBlank { null },
        )
    }

    private fun reduceAmountChange(
        state: BatchConversionState,
        amount: String,
    ): BatchConversionState {
        if (amount == state.amount) return state

        return when (state) {
            is BatchConversionState.Loading -> state.copy(amount = amount)
            is BatchConversionState.Content -> state.copy(
                baseCurrency = state.baseCurrency.copy(conversionValue = amount),
                favourites = state.favourites.map { item -> item.withAmount(amount) },
            )
        }
    }

    internal companion object {
        const val DEFAULT_AMOUNT = "1"
    }
}
