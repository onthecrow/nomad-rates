package com.onthecrow.nomadrates

import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.UIKit.UIDevice
import platform.Foundation.NSLog
import platform.Firebase.*

@Suppress("unused")
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val platformModule = module {  }
}

@OptIn(ExperimentalForeignApi::class)
actual fun initFirebase() {
    // Проверяем, не инициализирован ли он уже (на всякий случай)
    if (FIRApp.defaultApp() == null) {
        FIRApp.configure()
        NSLog("🔥 Firebase configured from Kotlin!")
    }
}
