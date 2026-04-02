package com.onthecrow.nomadrates.currency.di

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onthecrow.nomadrates.currency.CurrencyListReducer
import com.onthecrow.nomadrates.currency.CurrencyListDestination
import com.onthecrow.nomadrates.currency.CurrencyListScreen
import com.onthecrow.nomadrates.currency.CurrencyListViewModel
import com.onthecrow.nomadrates.navigation.registerScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val currencyModule = module {
    single { CurrencyListReducer() }

    viewModel { (selectionSource: com.onthecrow.nomadrates.currency.CurrencySelectionSource) ->
        CurrencyListViewModel(
            navigator = get(),
            screenResultDispatcher = get(),
            toggleCurrencyFavoriteUseCase = get(),
            observeShowFeaturedCurrenciesUseCase = get(),
            getCurrencyListUseCase = get(),
            reducer = get(),
            selectionSource = selectionSource,
        )
    }

    registerScreen<CurrencyListDestination> { destination, modifier ->
        val viewModel: CurrencyListViewModel = koinViewModel(
            parameters = { parametersOf(destination.source) }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()

        CurrencyListScreen(
            state = state,
            eventFlow = viewModel.eventFlow,
            modifier = modifier,
            onEvent = viewModel::onEvent,
        )
    }
}
