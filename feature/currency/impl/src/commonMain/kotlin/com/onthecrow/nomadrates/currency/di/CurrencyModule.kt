package com.onthecrow.nomadrates.currency.di

import com.onthecrow.nomadrates.currency.CurrencyListRoute
import com.onthecrow.nomadrates.currency.CurrencyListScreen
import com.onthecrow.nomadrates.currency.CurrencyListViewModel
import com.onthecrow.nomadrates.navigation.registerScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val currencyModule = module {
    viewModelOf(::CurrencyListViewModel)

    registerScreen<CurrencyListRoute> { _, modifier ->
        val viewModel: CurrencyListViewModel = koinViewModel()
        CurrencyListScreen(
            viewModel = viewModel,
            modifier = modifier,
        )
    }
}
