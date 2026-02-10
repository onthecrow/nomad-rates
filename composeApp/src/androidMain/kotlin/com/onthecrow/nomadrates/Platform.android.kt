package com.onthecrow.nomadrates

import android.app.Application
import android.content.Context
import android.os.Build
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.koin.dsl.module

class AndroidPlatform(val context: Application) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val platformModule = module {
        single<Context> { context }
        single<FirebaseRemoteConfig> {
            val app = FirebaseApp.getInstance()
            FirebaseRemoteConfig.getInstance(app)
        }
    }
}

actual fun initFirebase(platform: Platform) {
    Firebase.initialize((platform as AndroidPlatform).context)
}
