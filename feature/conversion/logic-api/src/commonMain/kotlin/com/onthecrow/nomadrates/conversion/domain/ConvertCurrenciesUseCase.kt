package com.onthecrow.nomadrates.conversion.domain

interface ConvertCurrenciesUseCase {
    suspend operator fun invoke(
        fromCurrencyCode: String,
        toCurrencyCode: String,
        amount: Double
    ): Double
}
