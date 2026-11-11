package com.onthecrow.nomadrates.favouritesconversion.di

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onthecrow.nomadrates.favouritesconversion.FavouritesConversionDestination
import com.onthecrow.nomadrates.favouritesconversion.FavouritesConversionReducer
import com.onthecrow.nomadrates.favouritesconversion.FavouritesConversionScreen
import com.onthecrow.nomadrates.favouritesconversion.FavouritesConversionViewModel
import com.onthecrow.nomadrates.navigation.registerScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val favouritesConversionModule = module {
    single { FavouritesConversionReducer() }

    viewModel { (destination: FavouritesConversionDestination) ->
        FavouritesConversionViewModel(
            navigator = get(),
            screenResultDispatcher = get(),
            observeFavouritesConversionUseCase = get(),
            observeUseEnglishCurrencyNamesUseCase = get(),
            setFavouritesConversionBaseCurrencyUseCase = get(),
            setCurrencyFavoriteUseCase = get(),
            reducer = get(),
            destination = destination,
        )
    }

    registerScreen<FavouritesConversionDestination> { destination, modifier ->
        val viewModel: FavouritesConversionViewModel = koinViewModel(
            parameters = { parametersOf(destination) }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()

        FavouritesConversionScreen(
            state = state,
            modifier = modifier,
            onEvent = viewModel::onEvent,
        )
    }
}
