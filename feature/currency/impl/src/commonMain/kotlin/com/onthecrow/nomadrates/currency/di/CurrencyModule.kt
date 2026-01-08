package com.onthecrow.nomadrates.currency.di

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onthecrow.nomadrates.currency.CurrencyListReducer
import com.onthecrow.nomadrates.currency.CurrencyListRoute
import com.onthecrow.nomadrates.currency.CurrencyListScreen
import com.onthecrow.nomadrates.currency.CurrencyListViewModel
import com.onthecrow.nomadrates.navigation.registerScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val currencyModule = module {
    single { CurrencyListReducer() }

    viewModelOf(::CurrencyListViewModel)

    registerScreen<CurrencyListRoute> { _, modifier ->
        val viewModel: CurrencyListViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        CurrencyListScreen(
            state = state,
            modifier = modifier,
            onEvent = viewModel::onEvent,
        )
    }
}
