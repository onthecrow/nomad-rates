package com.onthecrow.nomadrates.conversion.data

import com.onthecrow.nomadrates.conversion.data.database.ConversionDatabaseDataSource
import com.onthecrow.nomadrates.conversion.data.database.ConversionEntity
import com.onthecrow.nomadrates.conversion.mapper.toConversionEntity
import com.onthecrow.nomadrates.remoteconfig.RemoteConfigProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class ConversionRepository(
    private val conversionDatabaseDataSource: ConversionDatabaseDataSource,
    private val remoteConfigProvider: RemoteConfigProvider,
) {
    fun getConversionPairsFlow(): Flow<List<ConversionEntity>> {
        return channelFlow {
            // todo maybe it's need to be changed...
            remoteConfigProvider.getRemoteConfigFlow()
                .onEach { saveConversionsFromConfig(it.featuredConversions) }
                .launchIn(this)
            conversionDatabaseDataSource.getConversionPairs()
                .collect { conversionPairs ->
                    send(conversionPairs)
                }
        }
    }

    private suspend fun saveConversionsFromConfig(configConversions: List<Pair<String, String>>) {
        val conversions = conversionDatabaseDataSource.getConversionPairs().first()
        val conversionsToUpdate = configConversions.map { conversion ->
            val localConversion = conversions.find { conversionEntity -> conversionEntity.fromCurrencyCode == conversion.first && conversionEntity.toCurrencyCode == conversion.second }
            localConversion?.copy(isFeatured = true) ?: conversion.toConversionEntity().copy(isFeatured = true)
        }
        conversionDatabaseDataSource.saveConversions(conversionsToUpdate)
    }
}
