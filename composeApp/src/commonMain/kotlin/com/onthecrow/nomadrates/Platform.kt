package com.onthecrow.nomadrates

import org.koin.core.module.Module

interface Platform {
    val name: String
    val platformModule: Module
}
