package com.onthecrow.nomadrates.currency.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Firebase.FIRConfigUpdateListenerRegistration
import platform.Firebase.FIRRemoteConfig
import platform.Firebase.FIRRemoteConfigFetchAndActivateStatus
import platform.Foundation.NSError
import platform.Foundation.NSLog
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
        run {
            repeat(INITIAL_FETCH_RETRY_COUNT) { attempt ->
                val result = remoteConfig.awaitFetchAndActivate()
                if (result.isSuccessful) {
                    return@run true
                }

                NSLog(
                    "Currency Remote Config initial fetch failed (%d/%d): %s",
                    attempt + 1,
                    INITIAL_FETCH_RETRY_COUNT,
                    result.errorDescription ?: "Unknown error",
                )

                if (attempt != INITIAL_FETCH_RETRY_COUNT - 1) {
                    delay(INITIAL_FETCH_RETRY_DELAY_MS)
                }
            }

            false
        }

    override fun getString(key: String): String {
        return FIRRemoteConfig.remoteConfig().configValueForKey(key).stringValue
    }

    override fun getKeysByPrefix(prefix: String): Set<String> {
        return FIRRemoteConfig.remoteConfig().keysWithPrefix(prefix)
            .map { it.toString() }
            .toSet()
    }

    private fun NSError.toThrowable(fallback: String): Throwable =
        RuntimeException(localizedDescription.ifBlank { fallback })

    private suspend fun FIRRemoteConfig.awaitFetchAndActivate(): FetchAndActivateResult =
        suspendCancellableCoroutine { cont ->
            ensureInitializedWithCompletionHandler { initializationError ->
                if (!cont.isActive) return@ensureInitializedWithCompletionHandler

                if (initializationError != null) {
                    cont.resume(
                        FetchAndActivateResult(
                            isSuccessful = false,
                            errorDescription = initializationError.localizedDescription,
                        )
                    )
                    return@ensureInitializedWithCompletionHandler
                }

                fetchAndActivateWithCompletionHandler { status, error ->
                    if (!cont.isActive) return@fetchAndActivateWithCompletionHandler

                    val isSuccessful =
                        status == FIRRemoteConfigFetchAndActivateStatus.FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote ||
                            status == FIRRemoteConfigFetchAndActivateStatus.FIRRemoteConfigFetchAndActivateStatusSuccessUsingPreFetchedData

                    cont.resume(
                        FetchAndActivateResult(
                            isSuccessful = isSuccessful,
                            errorDescription = error?.localizedDescription,
                        )
                    )
                }
            }
        }

    private data class FetchAndActivateResult(
        val isSuccessful: Boolean,
        val errorDescription: String?,
    )

    private companion object {
        private const val INITIAL_FETCH_RETRY_COUNT = 3
        private const val INITIAL_FETCH_RETRY_DELAY_MS = 1_000L
    }
}
