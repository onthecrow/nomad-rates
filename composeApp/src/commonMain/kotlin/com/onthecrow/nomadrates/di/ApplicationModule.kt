package com.onthecrow.nomadrates.di

import com.onthecrow.nomadrates.currency.CurrencyListRoute
import com.onthecrow.nomadrates.currency.di.currencyLogicModule
import com.onthecrow.nomadrates.currency.di.currencyModule
import com.onthecrow.nomadrates.navigation.Destination
import com.onthecrow.nomadrates.navigation.di.StartDestination
import com.onthecrow.nomadrates.navigation.di.navigationModule
import kotlinx.serialization.json.Json
import org.koin.dsl.bind
import org.koin.dsl.module

val applicationModule = module {
    single { Json { ignoreUnknownKeys = true } }
    single(StartDestination) { CurrencyListRoute } bind Destination::class
    includes(
        navigationModule,
        currencyModule,
        currencyLogicModule,
    )
}
