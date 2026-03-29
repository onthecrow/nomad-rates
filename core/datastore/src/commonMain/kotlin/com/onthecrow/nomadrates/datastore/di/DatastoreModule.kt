package com.onthecrow.nomadrates.datastore.di

import org.koin.dsl.module

val datastoreModule = module {
    includes(datastorePlatformModule)
}
