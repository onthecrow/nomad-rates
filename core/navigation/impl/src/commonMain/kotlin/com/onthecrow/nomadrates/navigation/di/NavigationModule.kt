package com.onthecrow.nomadrates.navigation.di

import com.onthecrow.nomadrates.navigation.NavigationManager
import com.onthecrow.nomadrates.navigation.NavigationProvider
import com.onthecrow.nomadrates.navigation.Navigator
import com.onthecrow.nomadrates.navigation.NavigatorImpl
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val StartDestination = named("StartDestination")

val navigationModule = module {
    single { NavigatorImpl() } binds arrayOf(Navigator::class, NavigatorImpl::class)
    single {
        NavigationManager(
            navigator = get(),
            startDestination = get(StartDestination),
        )
    } bind NavigationProvider::class
}
