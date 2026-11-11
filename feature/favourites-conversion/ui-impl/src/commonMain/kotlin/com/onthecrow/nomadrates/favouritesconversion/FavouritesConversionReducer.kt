package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.favouritesconversion.mapper.toFavouritesConversionListItem
import com.onthecrow.nomadrates.favouritesconversion.mapper.withAmount
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
import com.onthecrow.nomadrates.uicore.Reducer
import com.onthecrow.nomadrates.util.filterAmountInput
import com.onthecrow.nomadrates.util.toAmountFieldValue

internal class FavouritesConversionReducer : Reducer<FavouritesConversionState, FavouritesConversionEvent> {
    override suspend fun reduce(
        state: FavouritesConversionState,
        event: FavouritesConversionEvent,
    ): FavouritesConversionState = when (event) {
        is FavouritesConversionEvent.OnFavouritesConversionChanged -> reduceFavouritesConversionChanged(state, event)
        is FavouritesConversionEvent.OnOpened -> reduceOpened(state, event)
        is FavouritesConversionEvent.OnAmountChange -> reduceAmountChange(
            state = state,
            amount = event.value.toAmountFieldValue(previous = state.amount),
        )
        is FavouritesConversionEvent.OnRemoveCurrency -> reduceRemoveCurrency(state, event)
        FavouritesConversionEvent.OnBackPress,
        FavouritesConversionEvent.OnBaseCurrencyClick,
        FavouritesConversionEvent.OnAddCurrencyClick -> state
    }

    private fun reduceFavouritesConversionChanged(
        state: FavouritesConversionState,
        event: FavouritesConversionEvent.OnFavouritesConversionChanged,
    ): FavouritesConversionState.Content {
        val amount = state.amount
        val favouritesConversion = event.favouritesConversion

        return FavouritesConversionState.Content(
            baseCurrency = CurrencyAmountViewState.Content.forCurrency(
                currencyCode = favouritesConversion.baseCurrency.code,
                conversionValue = amount,
            ),
            favourites = favouritesConversion.targets.map { target ->
                target.toFavouritesConversionListItem(
                    amount = amount,
                    useEnglishCurrencyNames = event.useEnglishCurrencyNames,
                )
            },
        )
    }

    private fun reduceOpened(
        state: FavouritesConversionState,
        event: FavouritesConversionEvent.OnOpened,
    ): FavouritesConversionState {
        val loadingState = state as? FavouritesConversionState.Loading ?: return state

        return loadingState.copy(
            amount = if (event.amount.filterAmountInput().isEmpty()) {
                loadingState.amount
            } else {
                event.amount.toAmountFieldValue(previous = "")
            },
            baseCurrencyCode = event.baseCurrencyCode.ifBlank { null },
        )
    }

    private fun reduceAmountChange(
        state: FavouritesConversionState,
        amount: String,
    ): FavouritesConversionState {
        if (amount == state.amount) return state

        return when (state) {
            is FavouritesConversionState.Loading -> state.copy(amount = amount)
            is FavouritesConversionState.Content -> state.copy(
                baseCurrency = state.baseCurrency.copy(conversionValue = amount),
                favourites = state.favourites.map { item -> item.withAmount(amount) },
            )
        }
    }

    /** Removes the row right away, before favourites are updated, so the swiped row does not come back. */
    private fun reduceRemoveCurrency(
        state: FavouritesConversionState,
        event: FavouritesConversionEvent.OnRemoveCurrency,
    ): FavouritesConversionState {
        val contentState = state as? FavouritesConversionState.Content ?: return state

        return contentState.copy(
            favourites = contentState.favourites.filterNot { item -> item.currencyCode == event.currencyCode },
        )
    }

    internal companion object {
        const val DEFAULT_AMOUNT = "1"
    }
}
