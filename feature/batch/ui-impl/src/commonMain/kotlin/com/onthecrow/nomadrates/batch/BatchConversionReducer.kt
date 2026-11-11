package com.onthecrow.nomadrates.batch

import com.onthecrow.nomadrates.batch.mapper.toBatchConversionListItem
import com.onthecrow.nomadrates.batch.mapper.withAmount
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
import com.onthecrow.nomadrates.uicore.Reducer
import com.onthecrow.nomadrates.util.filterAmountInput

internal class BatchConversionReducer : Reducer<BatchConversionState, BatchConversionEvent> {
    override suspend fun reduce(
        state: BatchConversionState,
        event: BatchConversionEvent,
    ): BatchConversionState = when (event) {
        is BatchConversionEvent.OnBatchConversionChanged -> reduceBatchConversionChanged(state, event)
        is BatchConversionEvent.OnOpened -> reduceOpened(state, event)
        is BatchConversionEvent.OnAmountChange -> reduceAmountChange(state, event.value.filterAmountInput())
        is BatchConversionEvent.OnRemoveCurrency -> reduceRemoveCurrency(state, event)
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
            baseCurrency = CurrencyAmountViewState.Content.forCurrency(
                currencyCode = batchConversion.baseCurrency.code,
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

    /** Removes the row right away, before favourites are updated, so the swiped row does not come back. */
    private fun reduceRemoveCurrency(
        state: BatchConversionState,
        event: BatchConversionEvent.OnRemoveCurrency,
    ): BatchConversionState {
        val contentState = state as? BatchConversionState.Content ?: return state

        return contentState.copy(
            favourites = contentState.favourites.filterNot { item -> item.currencyCode == event.currencyCode },
        )
    }

    internal companion object {
        const val DEFAULT_AMOUNT = "1"
    }
}
