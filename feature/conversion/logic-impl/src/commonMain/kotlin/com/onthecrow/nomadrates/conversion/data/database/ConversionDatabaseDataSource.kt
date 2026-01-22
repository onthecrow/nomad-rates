package com.onthecrow.nomadrates.conversion.data.database

import kotlinx.coroutines.flow.Flow

// TODO implement propper mapping here data -> domain, or may be move it to repository (?)
internal class ConversionDatabaseDataSource(
    private val conversionDao: ConversionDao,
) {
    fun getConversionPairs(): Flow<List<ConversionEntity>> {
        return conversionDao.getAllConversionsFlow()
    }

    suspend fun saveConversions(conversions: List<ConversionEntity>) {
        conversionDao.insertAll(conversions)
    }

    suspend fun saveConversion(conversion: ConversionEntity) {
        conversionDao.insert(conversion)
    }
}
