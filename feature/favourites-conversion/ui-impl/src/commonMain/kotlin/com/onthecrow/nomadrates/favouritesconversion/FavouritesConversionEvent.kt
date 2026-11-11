package com.onthecrow.nomadrates.favouritesconversion

import com.onthecrow.nomadrates.favouritesconversion.domain.model.FavouritesConversion
import com.onthecrow.nomadrates.uicore.Event

internal sealed interface FavouritesConversionEvent : Event {
    data object OnBackPress : FavouritesConversionEvent
    data object OnBaseCurrencyClick : FavouritesConversionEvent
    data object OnAddCurrencyClick : FavouritesConversionEvent

    /** The currency was swiped away: it is removed from favourites. */
    data class OnRemoveCurrency(val currencyCode: String) : FavouritesConversionEvent

    /**
     * What the screen was opened with: the base currency and the amount, both possibly blank.
     * A blank amount keeps the default amount.
     */
    data class OnOpened(
        val baseCurrencyCode: String,
        val amount: String,
    ) : FavouritesConversionEvent

    data class OnAmountChange(val value: String) : FavouritesConversionEvent
    data class OnFavouritesConversionChanged(
        val favouritesConversion: FavouritesConversion,
        val useEnglishCurrencyNames: Boolean,
    ) : FavouritesConversionEvent
}
