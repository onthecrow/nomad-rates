package com.onthecrow.nomadrates.favouritesconversion.di

import com.onthecrow.nomadrates.favouritesconversion.ObserveFavouritesConversionUseCaseImpl
import com.onthecrow.nomadrates.favouritesconversion.SetFavouritesConversionBaseCurrencyUseCaseImpl
import com.onthecrow.nomadrates.favouritesconversion.domain.ObserveFavouritesConversionUseCase
import com.onthecrow.nomadrates.favouritesconversion.domain.SetFavouritesConversionBaseCurrencyUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val favouritesConversionLogicModule: Module = module {
    single<ObserveFavouritesConversionUseCase> { ObserveFavouritesConversionUseCaseImpl(get(), get()) }
    single<SetFavouritesConversionBaseCurrencyUseCase> { SetFavouritesConversionBaseCurrencyUseCaseImpl(get(), get()) }
}
