package com.onthecrow.nomadrates.remoteconfig

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Firebase.FIRRemoteConfig
import platform.Firebase.FIRRemoteConfigFetchAndActivateStatus
import platform.Firebase.FIRRemoteConfigUpdate
import platform.Foundation.NSError
import platform.Foundation.NSLog
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
internal class IOSRemoteConfigProvider : RemoteConfigProviderImpl() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        initializeBackgroundSync()
    }

    override fun startBackgroundSync() {
        val remoteConfig = FIRRemoteConfig.remoteConfig()
        remoteConfig.addOnConfigUpdateListener { configUpdate, error ->
            if (error != null) {
                logError("Real-time Error", error.localizedDescription)
                return@addOnConfigUpdateListener
            }

            remoteConfig.activateWithCompletion { _, activateError ->
                if (activateError != null) {
                    logError("Activation Error", activateError.localizedDescription)
                } else {
                    checkUpdates(configUpdate)
                }
            }
        }

        refresh()
    }

    override fun refreshRemoteConfig() {
        scope.launch {
            val fetchSucceeded = FIRRemoteConfig.remoteConfig().fetchAndActivateWithRetry()
            if (fetchSucceeded) {
                onConfigUpdated()
            }
        }
    }

    private fun checkUpdates(configUpdate: FIRRemoteConfigUpdate?) {
        val updatedKeys = configUpdate?.updatedKeys ?: return

        configFields.forEach { configField ->
            if (updatedKeys.contains(configField)) {
                onConfigUpdated(configField)
            }
        }
    }

    override fun getString(key: String): String {
        return FIRRemoteConfig.remoteConfig().configValueForKey(key).stringValue
    }

    private suspend fun FIRRemoteConfig.fetchAndActivateWithRetry(): Boolean {
        repeat(INITIAL_FETCH_RETRY_COUNT) { attempt ->
            val result = awaitFetchAndActivate()
            if (result.isSuccessful) return true

            logError(
                tag = "Initial Fetch Error (${attempt + 1}/$INITIAL_FETCH_RETRY_COUNT)",
                message = result.errorDescription,
            )

            if (attempt != INITIAL_FETCH_RETRY_COUNT - 1) {
                delay(INITIAL_FETCH_RETRY_DELAY_MS)
            }
        }

        return false
    }

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

    private fun logError(tag: String, message: String?) {
        NSLog("$tag: ${message ?: "Unknown error"}")
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
