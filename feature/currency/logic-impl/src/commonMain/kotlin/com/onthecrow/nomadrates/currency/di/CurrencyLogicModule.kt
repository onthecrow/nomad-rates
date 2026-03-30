package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.domain.GetCurrencyListUseCase
import com.onthecrow.nomadrates.currency.GetCurrencyListUseCaseImpl
import com.onthecrow.nomadrates.currency.domain.GetCurrencyUseCase
import com.onthecrow.nomadrates.currency.GetCurrencyUseCaseImpl
import com.onthecrow.nomadrates.currency.ObserveCurrencyBootstrapStateUseCaseImpl
import com.onthecrow.nomadrates.currency.RefreshCurrenciesUseCaseImpl
import com.onthecrow.nomadrates.currency.ToggleCurrencyFavoriteUseCaseImpl
import com.onthecrow.nomadrates.currency.data.database.CurrencyDao
import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabase
import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseConstructor
import com.onthecrow.nomadrates.currency.data.database.CurrencyDatabaseDataSource
import com.onthecrow.nomadrates.currency.data.CurrencyRepository
import org.koin.dsl.module
import com.onthecrow.nomadrates.currency.data.CurrencyRepositoryImpl
import com.onthecrow.nomadrates.currency.domain.ObserveCurrencyBootstrapStateUseCase
import com.onthecrow.nomadrates.currency.domain.RefreshCurrenciesUseCase
import com.onthecrow.nomadrates.currency.domain.ToggleCurrencyFavoriteUseCase
import com.onthecrow.nomadrates.database.RoomFactory
import com.onthecrow.nomadrates.database.create
import org.koin.core.module.Module

val currencyLogicModule: Module = module {
    includes(currencyLogicPlatformModule)
    single<CurrencyRepository> { CurrencyRepositoryImpl(get(), get(), get()) }
    single<GetCurrencyListUseCase> { GetCurrencyListUseCaseImpl(get()) }
    single<GetCurrencyUseCase> { GetCurrencyUseCaseImpl(get()) }
    single<ObserveCurrencyBootstrapStateUseCase> { ObserveCurrencyBootstrapStateUseCaseImpl(get(), get()) }
    single<RefreshCurrenciesUseCase> { RefreshCurrenciesUseCaseImpl(get(), get()) }
    single<ToggleCurrencyFavoriteUseCase> { ToggleCurrencyFavoriteUseCaseImpl(get()) }
    single<CurrencyDao> {
        get<RoomFactory>().create<CurrencyDatabase>(
            name = "user.db",
            constructor = CurrencyDatabaseConstructor,
        )
            .currencyDao()
    }
    single<CurrencyDatabaseDataSource> { CurrencyDatabaseDataSource(get()) }
}
