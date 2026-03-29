package com.onthecrow.nomadrates.coroutines.di

import com.onthecrow.nomadrates.coroutines.ApplicationScopeProvider
import com.onthecrow.nomadrates.coroutines.DefaultApplicationScopeProvider
import com.onthecrow.nomadrates.coroutines.DefaultDispatchersProvider
import com.onthecrow.nomadrates.coroutines.DispatchersProvider
import org.koin.dsl.module

val coroutinesModule = module {
    single<DispatchersProvider> { DefaultDispatchersProvider }
    single<ApplicationScopeProvider> { DefaultApplicationScopeProvider(get()) }
}
