package com.onthecrow.nomadrates

import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.UIKit.UIDevice
import platform.Foundation.NSLog
import platform.Firebase.*

@OptIn(ExperimentalForeignApi::class)
@Suppress("unused")
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val platformModule = module {
        single<FIRRemoteConfig> { FIRRemoteConfig.remoteConfig() }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun initFirebase(platform: Platform) {
    if (FIRApp.defaultApp() == null) {
        FIRApp.configure()
        NSLog("🔥 Firebase configured from Kotlin!")
    }
}
