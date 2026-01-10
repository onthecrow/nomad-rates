package com.onthecrow.nomadrates.conversion.di

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onthecrow.nomadrates.conversion.ConversionDestination
import com.onthecrow.nomadrates.conversion.ConversionReducer
import com.onthecrow.nomadrates.conversion.ConversionScreen
import com.onthecrow.nomadrates.conversion.ConversionViewModel
import com.onthecrow.nomadrates.navigation.registerScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val conversionModule = module {
    single { ConversionReducer() }

    viewModelOf(::ConversionViewModel)

    registerScreen<ConversionDestination> { _, modifier ->
        val viewModel: ConversionViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        ConversionScreen(
            state = state,
            modifier = modifier,
            onEvent = viewModel::onEvent,
        )
    }
}
