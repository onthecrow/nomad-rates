package com.onthecrow.nomadrates.conversion.di

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onthecrow.nomadrates.conversion.BatchConversionDestination
import com.onthecrow.nomadrates.conversion.ConversionDestination
import com.onthecrow.nomadrates.conversion.ConversionReducer
import com.onthecrow.nomadrates.conversion.ConversionScreen
import com.onthecrow.nomadrates.conversion.ConversionViewModel
import com.onthecrow.nomadrates.conversion.batch.BatchConversionReducer
import com.onthecrow.nomadrates.conversion.batch.BatchConversionScreen
import com.onthecrow.nomadrates.conversion.batch.BatchConversionViewModel
import com.onthecrow.nomadrates.navigation.registerScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val conversionModule = module {
    single { ConversionReducer() }
    single { BatchConversionReducer() }

    viewModelOf(::ConversionViewModel)
    viewModel { (destination: BatchConversionDestination) ->
        BatchConversionViewModel(
            navigator = get(),
            screenResultDispatcher = get(),
            observeBatchConversionUseCase = get(),
            observeUseEnglishCurrencyNamesUseCase = get(),
            setBatchConversionBaseCurrencyUseCase = get(),
            addCurrencyToFavoritesUseCase = get(),
            reducer = get(),
            destination = destination,
        )
    }

    registerScreen<ConversionDestination> { _, modifier ->
        val viewModel: ConversionViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val showFeaturedPairs by viewModel.showFeaturedPairsStateFlow.collectAsStateWithLifecycle()

        ConversionScreen(
            state = state,
            showFeaturedPairs = showFeaturedPairs,
            modifier = modifier,
            eventFlow = viewModel.eventFlow,
            onEvent = viewModel::onEvent,
        )
    }

    registerScreen<BatchConversionDestination> { destination, modifier ->
        val viewModel: BatchConversionViewModel = koinViewModel(
            parameters = { parametersOf(destination) }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()

        BatchConversionScreen(
            state = state,
            modifier = modifier,
            onEvent = viewModel::onEvent,
        )
    }
}
