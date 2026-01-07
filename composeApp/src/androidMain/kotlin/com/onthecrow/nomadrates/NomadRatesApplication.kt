package com.onthecrow.nomadrates

import android.app.Application

class NomadRatesApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        getPlatform().initialize()
    }
}
