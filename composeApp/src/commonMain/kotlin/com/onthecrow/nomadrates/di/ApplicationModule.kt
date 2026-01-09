package com.onthecrow.nomadrates.di

import com.onthecrow.nomadrates.conversion.ConversionDestination
import com.onthecrow.nomadrates.conversion.di.conversionModule
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
    single(StartDestination) { ConversionDestination } bind Destination::class
    includes(
        navigationModule,
        conversionModule,
        currencyModule,
        currencyLogicModule,
    )
}
