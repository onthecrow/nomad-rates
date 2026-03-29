package com.onthecrow.nomadrates.datastore.di

import com.onthecrow.nomadrates.datastore.DataStoreFactory
import com.onthecrow.nomadrates.datastore.IOSDataStoreFactory
import org.koin.dsl.module

internal actual val datastorePlatformModule = module {
    single<DataStoreFactory> { IOSDataStoreFactory(get()) }
}
