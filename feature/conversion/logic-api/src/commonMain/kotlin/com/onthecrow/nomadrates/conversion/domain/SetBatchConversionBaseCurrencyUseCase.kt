package com.onthecrow.nomadrates.conversion.domain

interface SetBatchConversionBaseCurrencyUseCase {
    suspend operator fun invoke(currencyCode: String)
}
