package com.onthecrow.nomadrates.conversion.domain

interface ConvertCurrenciesUseCase {
    /**
     * Converts [amount] with the saved rates, or returns `null` when either currency has no saved
     * rate, so a missing rate never passes the amount through unconverted.
     */
    suspend operator fun invoke(
        fromCurrencyCode: String,
        toCurrencyCode: String,
        amount: Double
    ): Double?
}
