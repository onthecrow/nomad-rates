package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.data.datastore.ConversionPreferencesDataSource
import com.onthecrow.nomadrates.conversion.domain.SetBatchConversionBaseCurrencyUseCase

internal class SetBatchConversionBaseCurrencyUseCaseImpl(
    private val conversionPreferencesDataSource: ConversionPreferencesDataSource,
) : SetBatchConversionBaseCurrencyUseCase {
    override suspend fun invoke(currencyCode: String) {
        conversionPreferencesDataSource.saveBatchBaseCurrencyCode(currencyCode)
    }
}
