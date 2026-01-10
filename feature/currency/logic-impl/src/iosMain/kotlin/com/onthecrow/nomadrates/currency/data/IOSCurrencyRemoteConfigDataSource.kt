package com.onthecrow.nomadrates.currency.data

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Firebase.FIRRemoteConfig
import platform.Firebase.FIRRemoteConfigUpdate
import platform.Foundation.NSError
import platform.Foundation.NSLog

@OptIn(ExperimentalForeignApi::class)
internal class IOSCurrencyRemoteConfigDataSource : CurrencyRemoteConfigDataSource() {

    // TODO implement a proper di here
//    private val remoteConfig: FIRRemoteConfig by lazy { FIRRemoteConfig.remoteConfig() }

    // TODO check how it work in case: no internet -> has internet
    override fun startBackgroundSync() {
        FIRRemoteConfig.remoteConfig().addOnConfigUpdateListener { configUpdate, error ->
            if (error != null) {
                logError("Real-time Error", error)
                return@addOnConfigUpdateListener
            }

            // Changes subscription
            FIRRemoteConfig.remoteConfig().activateWithCompletion { _, activateError ->
                if (activateError != null) {
                    logError("Activation Error", activateError)
                } else {
                    checkUpdates(configUpdate)
                }
            }
        }

        // Initial loading
        FIRRemoteConfig.remoteConfig().fetchAndActivateWithCompletionHandler { status, error ->
            if (error != null) {
                logError("Initial Fetch Error", error)
            } else {
                updateData()
                updateHistoricalData()
            }
        }
    }

    private fun checkUpdates(configUpdate: FIRRemoteConfigUpdate?) {
        val updatedKeys = configUpdate?.updatedKeys ?: return

        if (updatedKeys.contains(KEY_DATA)) {
            updateData()
        }

        val stringKeys = updatedKeys.map { it.toString() }
        if (stringKeys.any { it.startsWith(PREFIX_CURRENCY) }) {
            updateHistoricalData()
        }
    }

    override fun getString(key: String): String {
        return FIRRemoteConfig.remoteConfig().configValueForKey(key).stringValue ?: ""
    }

    override fun getKeysByPrefix(prefix: String): Set<String> {
        return FIRRemoteConfig.remoteConfig().keysWithPrefix(prefix)
            .mapNotNull { it?.toString() }
            .toSet()
    }

    private fun logError(tag: String, error: NSError) {
        NSLog("$tag: ${error.localizedDescription}")
    }
}
