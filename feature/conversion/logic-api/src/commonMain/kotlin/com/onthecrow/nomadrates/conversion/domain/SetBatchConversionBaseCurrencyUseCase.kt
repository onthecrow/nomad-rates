package com.onthecrow.nomadrates.conversion.domain

/**
 * Changes the base currency of the batch conversion. The base currency is the "from" currency of
 * the selected conversion pair, so the main conversion screen picks the change up as well.
 */
interface SetBatchConversionBaseCurrencyUseCase {
    suspend operator fun invoke(currencyCode: String)
}
