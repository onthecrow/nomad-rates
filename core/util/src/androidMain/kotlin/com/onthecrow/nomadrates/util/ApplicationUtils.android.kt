package com.onthecrow.nomadrates.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import org.koin.core.component.get
import org.koin.core.context.GlobalContext

actual object ApplicationUtils {
    actual fun getAppVersion(): String {
        val context = GlobalContext.getOrNull()?.get<Context>() ?: return ""
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }

        return packageInfo.versionName.orEmpty()
    }

    actual fun currentTimeMillis(): Long = System.currentTimeMillis()
}
