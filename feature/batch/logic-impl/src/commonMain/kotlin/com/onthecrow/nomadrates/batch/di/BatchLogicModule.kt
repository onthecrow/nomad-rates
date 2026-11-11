package com.onthecrow.nomadrates.batch.di

import com.onthecrow.nomadrates.batch.ObserveBatchConversionUseCaseImpl
import com.onthecrow.nomadrates.batch.SetBatchConversionBaseCurrencyUseCaseImpl
import com.onthecrow.nomadrates.batch.domain.ObserveBatchConversionUseCase
import com.onthecrow.nomadrates.batch.domain.SetBatchConversionBaseCurrencyUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val batchLogicModule: Module = module {
    single<ObserveBatchConversionUseCase> { ObserveBatchConversionUseCaseImpl(get(), get()) }
    single<SetBatchConversionBaseCurrencyUseCase> { SetBatchConversionBaseCurrencyUseCaseImpl(get(), get()) }
}
