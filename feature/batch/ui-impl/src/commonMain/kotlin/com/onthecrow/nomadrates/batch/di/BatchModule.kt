package com.onthecrow.nomadrates.batch.di

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onthecrow.nomadrates.batch.BatchConversionDestination
import com.onthecrow.nomadrates.batch.BatchConversionReducer
import com.onthecrow.nomadrates.batch.BatchConversionScreen
import com.onthecrow.nomadrates.batch.BatchConversionViewModel
import com.onthecrow.nomadrates.navigation.registerScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val batchModule = module {
    single { BatchConversionReducer() }

    viewModel { (destination: BatchConversionDestination) ->
        BatchConversionViewModel(
            navigator = get(),
            screenResultDispatcher = get(),
            observeBatchConversionUseCase = get(),
            observeUseEnglishCurrencyNamesUseCase = get(),
            setBatchConversionBaseCurrencyUseCase = get(),
            setCurrencyFavoriteUseCase = get(),
            reducer = get(),
            destination = destination,
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
