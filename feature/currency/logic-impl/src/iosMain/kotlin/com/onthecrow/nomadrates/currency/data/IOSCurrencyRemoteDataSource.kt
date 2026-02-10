package com.onthecrow.nomadrates.currency.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Firebase.FIRConfigUpdateListenerRegistration
import platform.Firebase.FIRRemoteConfig
import platform.Firebase.FIRRemoteConfigFetchAndActivateStatus
import platform.Foundation.NSError
import kotlin.coroutines.resume


@OptIn(ExperimentalForeignApi::class)
internal class IOSCurrencyRemoteDataSource(
    private val remoteConfig: FIRRemoteConfig,
) : CurrencyRemoteDataSource() {

    private var listenerRegistration: FIRConfigUpdateListenerRegistration? = null

    init {
        start()
    }

    override fun startBackgroundSync(
        onActivated: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        listenerRegistration = remoteConfig.addOnConfigUpdateListener { _, error ->
            if (error != null) {
                onError(error.toThrowable("Remote Config update listener error"))
                return@addOnConfigUpdateListener
            }

            remoteConfig.activateWithCompletion { _, activateError ->
                if (activateError != null) {
                    onError(activateError.toThrowable("Remote Config activate error"))
                } else {
                    onActivated()
                }
            }
        }
    }

    override suspend fun fetchAndActivate(): Boolean =
        suspendCancellableCoroutine { cont ->
            remoteConfig.fetchAndActivateWithCompletionHandler { status, error ->
                if (!cont.isActive) return@fetchAndActivateWithCompletionHandler

                if (error != null) {
                    cont.resume(false)
                    return@fetchAndActivateWithCompletionHandler
                }

                val ok =
                    status == FIRRemoteConfigFetchAndActivateStatus.FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote ||
                            status == FIRRemoteConfigFetchAndActivateStatus.FIRRemoteConfigFetchAndActivateStatusSuccessUsingPreFetchedData

                cont.resume(ok)
            }
        }
    // TODO check how it work in case: no internet -> has internet

    override fun getString(key: String): String {
        return FIRRemoteConfig.remoteConfig().configValueForKey(key).stringValue ?: ""
    }

    override fun getKeysByPrefix(prefix: String): Set<String> {
        return FIRRemoteConfig.remoteConfig().keysWithPrefix(prefix)
            .mapNotNull { it?.toString() }
            .toSet()
    }

    private fun NSError.toThrowable(fallback: String): Throwable =
        RuntimeException(localizedDescription ?: fallback)
}
