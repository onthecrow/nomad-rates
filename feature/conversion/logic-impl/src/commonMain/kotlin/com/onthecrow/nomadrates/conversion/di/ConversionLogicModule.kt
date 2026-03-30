package com.onthecrow.nomadrates.conversion.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.onthecrow.nomadrates.conversion.ConvertCurrenciesUseCaseImpl
import com.onthecrow.nomadrates.conversion.GetConversionPairUseCaseImpl
import com.onthecrow.nomadrates.conversion.GetConversionPairsUseCaseImpl
import com.onthecrow.nomadrates.conversion.GetHistoricalRatesUseCaseImpl
import com.onthecrow.nomadrates.conversion.GetSelectedConversionPairUseCaseImpl
import com.onthecrow.nomadrates.conversion.ObserveConversionDataUseCaseImpl
import com.onthecrow.nomadrates.conversion.SaveSelectedConversionPairUseCaseImpl
import com.onthecrow.nomadrates.conversion.ToggleConversionPairFavouriteUseCaseImpl
import com.onthecrow.nomadrates.conversion.data.ConversionRepository
import com.onthecrow.nomadrates.conversion.data.ConversionSelectionRepository
import com.onthecrow.nomadrates.conversion.data.database.ConversionDao
import com.onthecrow.nomadrates.conversion.data.database.ConversionDatabase
import com.onthecrow.nomadrates.conversion.data.database.ConversionDatabaseConstructor
import com.onthecrow.nomadrates.conversion.data.database.ConversionDatabaseDataSource
import com.onthecrow.nomadrates.conversion.data.datastore.ConversionPreferencesDataSource
import com.onthecrow.nomadrates.conversion.domain.ConvertCurrenciesUseCase
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.GetConversionPairsUseCase
import com.onthecrow.nomadrates.conversion.domain.GetHistoricalRatesUseCase
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ObserveConversionDataUseCase
import com.onthecrow.nomadrates.conversion.domain.SaveSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.ToggleConversionPairFavouriteUseCase
import com.onthecrow.nomadrates.datastore.DataStoreFactory
import com.onthecrow.nomadrates.database.RoomFactory
import com.onthecrow.nomadrates.database.create
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val CONVERSION_PREFERENCES_DATA_STORE_QUALIFIER = "conversion_preferences_data_store"
private const val CONVERSION_PREFERENCES_DATA_STORE_NAME = "conversion.preferences_pb"

val conversionLogicModule: Module = module {
    single<ConvertCurrenciesUseCase> { ConvertCurrenciesUseCaseImpl(get()) }
    single<GetHistoricalRatesUseCase> { GetHistoricalRatesUseCaseImpl(get()) }
    single<GetConversionPairUseCase> { GetConversionPairUseCaseImpl(get(), get(), get()) }
    single<GetSelectedConversionPairUseCase> { GetSelectedConversionPairUseCaseImpl(get()) }
    single<ObserveConversionDataUseCase> { ObserveConversionDataUseCaseImpl(get(), get(), get(), get()) }
    single<SaveSelectedConversionPairUseCase> { SaveSelectedConversionPairUseCaseImpl(get()) }
    single<ToggleConversionPairFavouriteUseCase> { ToggleConversionPairFavouriteUseCaseImpl(get()) }
    factory<GetConversionPairsUseCase> { GetConversionPairsUseCaseImpl(get(), get(), get()) }
    single { ConversionRepository(get(), get()) }
    single<ConversionSelectionRepository> { ConversionSelectionRepository(get()) }
    single<DataStore<Preferences>>(named(CONVERSION_PREFERENCES_DATA_STORE_QUALIFIER)) {
        get<DataStoreFactory>().createPreferencesDataStore(CONVERSION_PREFERENCES_DATA_STORE_NAME)
    }
    single<ConversionPreferencesDataSource> {
        ConversionPreferencesDataSource(get(named(CONVERSION_PREFERENCES_DATA_STORE_QUALIFIER)))
    }
    single<ConversionDao> {
        get<RoomFactory>().create<ConversionDatabase>(
            name = "conversion.db",
            constructor = ConversionDatabaseConstructor,
        )
            .conversionDao()
    }
    single<ConversionDatabaseDataSource> { ConversionDatabaseDataSource(get()) }
}
