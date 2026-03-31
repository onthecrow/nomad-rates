package com.onthecrow.nomadrates.settings.di

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onthecrow.nomadrates.navigation.registerScreen
import com.onthecrow.nomadrates.settings.SettingsDestination
import com.onthecrow.nomadrates.settings.SettingsReducer
import com.onthecrow.nomadrates.settings.SettingsScreen
import com.onthecrow.nomadrates.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    single { SettingsReducer() }

    viewModelOf(::SettingsViewModel)

    registerScreen<SettingsDestination> { _, modifier ->
        val viewModel: SettingsViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        SettingsScreen(
            state = state,
            modifier = modifier,
            onEvent = viewModel::onEvent,
        )
    }
}
