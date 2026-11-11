package com.onthecrow.nomadrates.conversion.batch

import com.onthecrow.nomadrates.conversion.domain.model.BatchConversion
import com.onthecrow.nomadrates.uicore.Event

internal sealed interface BatchConversionEvent : Event {
    data object OnBackPress : BatchConversionEvent
    data object OnBaseCurrencyClick : BatchConversionEvent
    data object OnAddCurrencyClick : BatchConversionEvent

    /**
     * What the screen was opened with: the base currency and the amount, both possibly blank.
     * A blank amount keeps the default amount.
     */
    data class OnOpened(
        val baseCurrencyCode: String,
        val amount: String,
    ) : BatchConversionEvent
    data class OnAmountChange(val value: String) : BatchConversionEvent
    data class OnBatchConversionChanged(
        val batchConversion: BatchConversion,
        val useEnglishCurrencyNames: Boolean,
    ) : BatchConversionEvent
}
