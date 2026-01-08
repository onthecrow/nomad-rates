package com.onthecrow.nomadrates.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val applicationModule = module {
    single { Json { ignoreUnknownKeys = true } }
}
