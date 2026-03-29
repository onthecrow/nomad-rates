package com.onthecrow.nomadrates.conversion.data

import com.onthecrow.nomadrates.conversion.data.datastore.ConversionPreferencesDataSource
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import kotlinx.coroutines.flow.Flow

internal class ConversionSelectionRepository(
    private val conversionPreferencesDataSource: ConversionPreferencesDataSource,
) {
    fun getSelectedConversionPair(): Flow<SelectedConversionPair?> {
        return conversionPreferencesDataSource.getSelectedConversionPair()
    }

    suspend fun saveSelectedConversionPair(selectedConversionPair: SelectedConversionPair) {
        conversionPreferencesDataSource.saveSelectedConversionPair(selectedConversionPair)
    }
}
