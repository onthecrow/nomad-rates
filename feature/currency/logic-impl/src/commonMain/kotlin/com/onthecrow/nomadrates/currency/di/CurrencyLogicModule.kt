package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.GetCurrencyListUseCaseImpl
import com.onthecrow.nomadrates.currency.domain.GetCurrencyUseCase
import com.onthecrow.nomadrates.currency.GetCurrencyUseCaseImpl
import com.onthecrow.nomadrates.currency.ObserveCurrencyBootstrapStateUseCaseImpl
import com.onthecrow.nomadrates.currency.ObserveLastRatesTimestampUseCaseImpl
import com.onthecrow.nomadrates.currency.RefreshCurrenciesUseCaseImpl
import com.onthecrow.nomadrates.currency.RefreshRatesManuallyUseCaseImpl
import com.onthecrow.nomadrates.currency.ToggleCurrencyFavoriteUseCaseImpl
import com.onthecrow.nomadrates.currency.data.database.CurrencyDao
import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabase
import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseConstructor
import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseDataSource
import com.onthecrow.nomadrates.currency.data.datastore.CurrencyRatesMetadataDataSource
import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import org.koin.dsl.module
import com.onthecrow.nomadrates.currency.data.CurrencyRepositoryImpl
import com.onthecrow.nomadrates.currency.domain.ObserveCurrencyBootstrapStateUseCase
import com.onthecrow.nomadrates.currency.domain.ObserveLastRatesTimestampUseCase
import com.onthecrow.nomadrates.currency.domain.RefreshCurrenciesUseCase
import com.onthecrow.nomadrates.currency.domain.RefreshRatesManuallyUseCase
import com.onthecrow.nomadrates.currency.domain.ToggleCurrencyFavoriteUseCase
import com.onthecrow.nomadrates.datastore.DataStoreFactory
import com.onthecrow.nomadrates.database.RoomFactory
import com.onthecrow.nomadrates.database.create
import org.koin.core.module.Module

val currencyLogicModule: Module = module {
    includes(currencyLogicPlatformModule)
    single<CurrencyRepository> { CurrencyRepositoryImpl(get(), get(), get(), get()) }
    single<GetCurrencyListUseCase> { GetCurrencyListUseCaseImpl(get()) }
    single<GetCurrencyUseCase> { GetCurrencyUseCaseImpl(get()) }
    single<ObserveCurrencyBootstrapStateUseCase> { ObserveCurrencyBootstrapStateUseCaseImpl(get(), get()) }
    single<RefreshCurrenciesUseCase> { RefreshCurrenciesUseCaseImpl(get(), get()) }
    single<ObserveLastRatesTimestampUseCase> { ObserveLastRatesTimestampUseCaseImpl(get()) }
    single<RefreshRatesManuallyUseCase> { RefreshRatesManuallyUseCaseImpl(get(), get(), get()) }
    single<ToggleCurrencyFavoriteUseCase> { ToggleCurrencyFavoriteUseCaseImpl(get()) }
    single<CurrencyRatesMetadataDataSource> {
        CurrencyRatesMetadataDataSource(
            get<DataStoreFactory>().createPreferencesDataStore(name = "currency_metadata.preferences_pb")
        )
    }
    single<CurrencyDao> {
        get<RoomFactory>().create<CurrencyDatabase>(
            name = "user.db",
            constructor = CurrencyDatabaseConstructor,
        )
            .currencyDao()
    }
    single<CurrencyDatabaseDataSource> { CurrencyDatabaseDataSource(get()) }
}
