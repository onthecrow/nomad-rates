package com.onthecrow.nomadrates.settings.di

import com.onthecrow.nomadrates.datastore.DataStoreFactory
import com.onthecrow.nomadrates.settings.ObserveDefaultPairUseCaseImpl
import com.onthecrow.nomadrates.settings.ObserveLaunchPairModeUseCaseImpl
import com.onthecrow.nomadrates.settings.ObserveSettingsLinksUseCaseImpl
import com.onthecrow.nomadrates.settings.ObserveShowFeaturedCurrenciesUseCaseImpl
import com.onthecrow.nomadrates.settings.ObserveShowFeaturedPairsUseCaseImpl
import com.onthecrow.nomadrates.settings.ObserveThemeModeUseCaseImpl
import com.onthecrow.nomadrates.settings.SetDefaultPairUseCaseImpl
import com.onthecrow.nomadrates.settings.SetLaunchPairModeUseCaseImpl
import com.onthecrow.nomadrates.settings.SetShowFeaturedCurrenciesUseCaseImpl
import com.onthecrow.nomadrates.settings.SetShowFeaturedPairsUseCaseImpl
import com.onthecrow.nomadrates.settings.SetThemeModeUseCaseImpl
import com.onthecrow.nomadrates.settings.data.SettingsRepository
import com.onthecrow.nomadrates.settings.data.SettingsRepositoryImpl
import com.onthecrow.nomadrates.settings.data.datastore.SettingsPreferencesDataSource
import com.onthecrow.nomadrates.settings.domain.ObserveDefaultPairUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveLaunchPairModeUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveSettingsLinksUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveShowFeaturedCurrenciesUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveShowFeaturedPairsUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveThemeModeUseCase
import com.onthecrow.nomadrates.settings.domain.SetDefaultPairUseCase
import com.onthecrow.nomadrates.settings.domain.SetLaunchPairModeUseCase
import com.onthecrow.nomadrates.settings.domain.SetShowFeaturedCurrenciesUseCase
import com.onthecrow.nomadrates.settings.domain.SetShowFeaturedPairsUseCase
import com.onthecrow.nomadrates.settings.domain.SetThemeModeUseCase
import org.koin.dsl.module

private const val SETTINGS_PREFERENCES_NAME = "settings.preferences_pb"

val settingsLogicModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ObserveLaunchPairModeUseCase> { ObserveLaunchPairModeUseCaseImpl(get()) }
    single<SetLaunchPairModeUseCase> { SetLaunchPairModeUseCaseImpl(get()) }
    single<ObserveDefaultPairUseCase> { ObserveDefaultPairUseCaseImpl(get()) }
    single<ObserveSettingsLinksUseCase> { ObserveSettingsLinksUseCaseImpl(get()) }
    single<SetDefaultPairUseCase> { SetDefaultPairUseCaseImpl(get()) }
    single<ObserveShowFeaturedPairsUseCase> { ObserveShowFeaturedPairsUseCaseImpl(get()) }
    single<SetShowFeaturedPairsUseCase> { SetShowFeaturedPairsUseCaseImpl(get()) }
    single<ObserveShowFeaturedCurrenciesUseCase> { ObserveShowFeaturedCurrenciesUseCaseImpl(get()) }
    single<SetShowFeaturedCurrenciesUseCase> { SetShowFeaturedCurrenciesUseCaseImpl(get()) }
    single<ObserveThemeModeUseCase> { ObserveThemeModeUseCaseImpl(get()) }
    single<SetThemeModeUseCase> { SetThemeModeUseCaseImpl(get()) }
    single {
        SettingsPreferencesDataSource(
            dataStore = get<DataStoreFactory>().createPreferencesDataStore(SETTINGS_PREFERENCES_NAME),
        )
    }
}
