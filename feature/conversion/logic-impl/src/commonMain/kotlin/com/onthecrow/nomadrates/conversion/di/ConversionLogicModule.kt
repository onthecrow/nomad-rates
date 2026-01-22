package com.onthecrow.nomadrates.conversion.di

import com.onthecrow.nomadrates.conversion.ConvertCurrenciesUseCaseImpl
import com.onthecrow.nomadrates.conversion.GetConversionPairsUseCaseImpl
import com.onthecrow.nomadrates.conversion.GetHistoricalRatesUseCaseImpl
import com.onthecrow.nomadrates.conversion.data.ConversionRepository
import com.onthecrow.nomadrates.conversion.data.database.ConversionDao
import com.onthecrow.nomadrates.conversion.data.database.ConversionDatabase
import com.onthecrow.nomadrates.conversion.data.database.ConversionDatabaseConstructor
import com.onthecrow.nomadrates.conversion.data.database.ConversionDatabaseDataSource
import com.onthecrow.nomadrates.conversion.domain.ConvertCurrenciesUseCase
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairsUseCase
import com.onthecrow.nomadrates.conversion.domain.GetHistoricalRatesUseCase
import com.onthecrow.nomadrates.database.RoomFactory
import com.onthecrow.nomadrates.database.create
import org.koin.core.module.Module
import org.koin.dsl.module

val conversionLogicModule: Module = module {
    single<ConvertCurrenciesUseCase> { ConvertCurrenciesUseCaseImpl(get()) }
    single<GetHistoricalRatesUseCase> { GetHistoricalRatesUseCaseImpl(get()) }
    factory<GetConversionPairsUseCase> { GetConversionPairsUseCaseImpl(get(), get(), get()) }
    single { ConversionRepository(get(), get()) }
    single<ConversionDao> {
        get<RoomFactory>().create<ConversionDatabase>(
            name = "conversion.db",
            constructor = ConversionDatabaseConstructor,
        )
            .conversionDao()
    }
    single<ConversionDatabaseDataSource> { ConversionDatabaseDataSource(get()) }
}
