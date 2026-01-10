package com.onthecrow.nomadrates.conversion.di

import com.onthecrow.nomadrates.conversion.ConvertCurrenciesUseCaseImpl
import com.onthecrow.nomadrates.conversion.domain.ConvertCurrenciesUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val conversionLogicModule: Module = module {
    single<ConvertCurrenciesUseCase> { ConvertCurrenciesUseCaseImpl(get()) }
}
