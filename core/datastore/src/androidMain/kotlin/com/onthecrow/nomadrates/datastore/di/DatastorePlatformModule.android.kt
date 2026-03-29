package com.onthecrow.nomadrates.datastore.di

import com.onthecrow.nomadrates.datastore.AndroidDataStoreFactory
import com.onthecrow.nomadrates.datastore.DataStoreFactory
import org.koin.dsl.module

internal actual val datastorePlatformModule = module {
    single<DataStoreFactory> { AndroidDataStoreFactory(get(), get()) }
}
